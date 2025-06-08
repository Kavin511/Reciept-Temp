package com.devstudio.receipto.auth

import kotlinx.coroutines.flow.Flow

// Data class for user information
data class PlatformUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?
)

// Result wrapper for operations
sealed class AuthResult {
    data class Success(val user: PlatformUser?) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Cancelled : AuthResult() // Added for operations that can be cancelled by user
}

expect class AuthService {
    // Observe current authentication state
    fun observeAuthState(): Flow<PlatformUser?>

    // Initiate Google Sign-In
    // The actual implementation will need to handle platform-specific UI interactions.
    suspend fun signInWithGoogle(): AuthResult

    // Sign out
    suspend fun signOut(): AuthResult

    // Delete account
    // This operation might require re-authentication, which should be handled
    // within the actual implementation or indicated in the result.
    suspend fun deleteAccount(): AuthResult

    // Get current user
    fun getCurrentUser(): PlatformUser?
}
