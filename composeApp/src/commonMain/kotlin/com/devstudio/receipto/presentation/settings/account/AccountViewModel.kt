package com.devstudio.receipto.presentation.settings.account

import com.devstudio.receipto.auth.AuthResult
import com.devstudio.receipto.auth.AuthService
import com.devstudio.receipto.auth.PlatformUser // Ensure this is accessible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val authService: AuthService,
    // viewModelScope allows passing a scope, e.g., from a KMP ViewModel library or test
    // Defaults to a new scope that should be managed by the ViewModel's lifecycle.
    // Making it public for Android specific Composable to launch coroutine for result handling.
    val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
) {
    private val _uiState = MutableStateFlow(AccountScreenState())
    val uiState: StateFlow<AccountScreenState> = _uiState.asStateFlow()

    // New state property for Android UI to observe
    private val _initiateGoogleSignInEvent = MutableStateFlow(false)
    val initiateGoogleSignInEvent: StateFlow<Boolean> = _initiateGoogleSignInEvent.asStateFlow()

    init {
        authService.observeAuthState()
            .onEach { platformUser ->
                _uiState.update { currentState ->
                    currentState.copy(
                        currentUser = platformUser,
                        isLoading = false, // Reset loading on auth change
                        error = null,      // Reset error on auth change
                        requiresReAuthentication = false // Reset re-auth flag
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // This method will be called by the common UI (AccountSection)
    fun triggerSignInWithGoogle() {
        // For iOS, authService.signInWithGoogle() might work directly if it handles UI.
        // For Android, we need to signal the UI.
        // A more KMP-idiomatic way might be for authService.signInWithGoogle() to return
        // a specific result indicating UI action is needed on certain platforms.
        // For now, let's assume commonMain calls this, and Android UI observes initiateGoogleSignInEvent.
        // iOS actual of authService.signInWithGoogle() will perform the sign-in.
        // Android actual of authService.signInWithGoogle() returns an error, so this event is needed.

        if (authService.getCurrentUser() != null) return // Already signed in

        // If on Android, this will trigger the UI event.
        // If on iOS, this will directly attempt sign-in via the actual AuthService.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, requiresReAuthentication = false) }
            // The expect/actual of signInWithGoogle handles platform differences.
            // Android's actual returns an error "Platform-specific UI interaction required",
            // So, we set an event for Android UI to pick up.
            // iOS's actual will attempt the sign-in.
            val result = authService.signInWithGoogle() // This call is still needed for iOS.

            // Handle result for iOS or if Android had a direct (non-UI-triggering) path
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                is AuthResult.Error -> {
                    // If this error is the specific one from Android's actual AuthService, trigger UI.
                    if (result.message.contains("Platform-specific UI interaction required")) {
                        _initiateGoogleSignInEvent.value = true
                        // isLoading will be reset by UI after attempt or by authStateChange
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
                is AuthResult.Cancelled -> _uiState.update {
                    it.copy(isLoading = false, error = "Sign-in cancelled.")
                }
            }
        }
    }

    // Call this after the Android UI has consumed the event
    fun onGoogleSignInLaunched() {
        _initiateGoogleSignInEvent.value = false
        // Keep isLoading = true until handleGoogleSignInResult provides an outcome
        // or authState changes.
    }

    // This new method will be called from Android UI after the activity result
    fun processGoogleSignInResult(authResult: AuthResult) {
        _uiState.update {
            it.copy(
                isLoading = false, // Operation finished
                error = if (authResult is AuthResult.Error) authResult.message else null,
                // currentUser will be updated by observeAuthState if successful
            )
        }
    }

    // Removed the generic handleGoogleSignInIntent as the Android specific Composable will handle it.

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, requiresReAuthentication = false) }
            when (val result = authService.signOut()) {
                is AuthResult.Success -> {
                    // User state (currentUser = null) will be updated by observeAuthState.
                    // isLoading will also be reset by observeAuthState if it emits.
                    // Explicitly clear loading here to be sure.
                     _uiState.update { it.copy(isLoading = false) }
                }
                is AuthResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, requiresReAuthentication = false) }
            when (val result = authService.signOut()) {
                is AuthResult.Success -> {
                    // User state (currentUser = null) will be updated by observeAuthState.
                    // isLoading will also be reset by observeAuthState if it emits.
                    // Explicitly clear loading here to be sure.
                     _uiState.update { it.copy(isLoading = false) }
                }
                is AuthResult.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is AuthResult.Cancelled -> _uiState.update { // Should not typically happen for signout
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, requiresReAuthentication = false) }
            when (val result = authService.deleteAccount()) {
                is AuthResult.Success -> {
                    // User state (currentUser = null) will be updated by observeAuthState.
                    // isLoading will also be reset by observeAuthState.
                    // Explicitly clear loading here.
                    _uiState.update { it.copy(isLoading = false) }
                }
                is AuthResult.Error -> {
                    // A more robust solution would involve specific error codes/types from AuthService
                    // For example, if AuthService.deleteAccount() returned a specific ReAuthenticationRequiredResult.
                    val needsReAuth = result.message.contains("Re-authentication", ignoreCase = true) ||
                                      result.message.contains("requires recent sign-in", ignoreCase = true)
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message, requiresReAuthentication = needsReAuth)
                    }
                }
                 is AuthResult.Cancelled -> _uiState.update { // Should not typically happen for delete
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearReAuthFlag() {
        _uiState.update { it.copy(requiresReAuthentication = false) }
    }

    /**
     * Call this method when the ViewModel is no longer needed to cancel its CoroutineScope.
     * This is important to prevent leaks and stop ongoing operations if the ViewModel is cleared.
     * In Android, this would be called in `onCleared()`. For other platforms or a pure KMP ViewModel,
     * the lifecycle owner is responsible for calling this.
     */
    fun onClear() {
        viewModelScope.cancel() // Cancels the SupervisorJob and all its children
    }
}
