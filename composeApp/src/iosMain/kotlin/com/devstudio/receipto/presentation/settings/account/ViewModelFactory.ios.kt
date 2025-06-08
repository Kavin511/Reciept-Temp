package com.devstudio.receipto.presentation.settings.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.devstudio.receipto.auth.AuthService

@Composable
actual fun rememberAccountViewModel(): AccountViewModel {
    // AuthService actual class for iOS doesn't require context in constructor
    val authService = remember { AuthService() }
    // AccountViewModel creates its own default scope.
    return remember { AccountViewModel(authService) }
}
