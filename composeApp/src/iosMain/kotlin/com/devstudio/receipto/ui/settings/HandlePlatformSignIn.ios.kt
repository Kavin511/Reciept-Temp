package com.devstudio.receipto.ui

import androidx.compose.runtime.Composable
import com.devstudio.receipto.presentation.settings.account.AccountViewModel

@Composable
actual fun HandlePlatformSignIn(
    shouldTriggerSignIn: Boolean,
    onSignInLaunched: () -> Unit,
    viewModel: AccountViewModel
) {
    // No specific UI handling needed here for iOS.
    // The call to viewModel.triggerSignInWithGoogle() will directly
    // invoke authService.signInWithGoogle(), and the iOS actual
    // implementation of AuthService handles the UI flow (presenting view controller).
    // onSignInLaunched can be called to maintain event consistency if needed,
    // though it's primarily for Android's async activity launch.
    if (shouldTriggerSignIn) {
        onSignInLaunched() // Consume the event, actual sign-in is in iOS AuthService
    }
}
