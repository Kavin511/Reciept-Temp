package com.devstudio.receipto.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.devstudio.receipto.auth.AuthService
import com.devstudio.receipto.presentation.settings.account.AccountViewModel
import kotlinx.coroutines.launch


@Composable
actual fun HandlePlatformSignIn(
    shouldTriggerSignIn: Boolean,
    onSignInLaunched: () -> Unit,
    viewModel: AccountViewModel
) {
    val context = LocalContext.current
    // Get an instance of AuthService specific to Android context.
    // This assumes AuthService has an actual constructor taking Context or is accessible.
    // If using DI, this would be injected. For simplicity, instantiating here.
    // This is okay because this actual composable is only for Android.
    val authService = rememberAuthServiceForPlatform(context)


    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            // viewModelScope is not directly available here.
            // The AccountViewModel's scope should be used.
            viewModel.viewModelScope.launch { // Use viewModel's scope
                val authResult = AuthService.handleGoogleSignInResult(intent, authService)
                viewModel.processGoogleSignInResult(authResult)
            }
        } else {
            // Handle cancellation or error from the activity result itself if needed,
            // though handleGoogleSignInResult also has cancellation logic.
            viewModel.processGoogleSignInResult(com.devstudio.receipto.auth.AuthResult.Cancelled)
        }
    }

    LaunchedEffect(shouldTriggerSignIn) {
        if (shouldTriggerSignIn) {
            val signInIntent = AuthService.getGoogleSignInIntent(context)
            googleSignInLauncher.launch(signInIntent)
            onSignInLaunched() // Notify ViewModel that the intent has been launched
        }
    }
}

// Helper to remember AuthService instance with context, specific to this Android screen.
// In a DI setup, AuthService would be injected into the ViewModel, and not created here.
@Composable
private fun rememberAuthServiceForPlatform(context: Context): AuthService {
    return remember { AuthService(context.applicationContext) }
}
