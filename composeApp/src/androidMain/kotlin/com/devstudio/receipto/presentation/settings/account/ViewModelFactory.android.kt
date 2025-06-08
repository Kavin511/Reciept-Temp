package com.devstudio.receipto.presentation.settings.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.devstudio.receipto.auth.AuthService

@Composable
actual fun rememberAccountViewModel(): AccountViewModel {
    val context = LocalContext.current.applicationContext
    // AuthService itself is an actual class, its constructor takes context on Android.
    val authService = remember { AuthService(context) }
    // Pass the same CoroutineScope if you want to tie them, or let ViewModel create its own.
    // For this setup, AccountViewModel creates its own default scope.
    return remember { AccountViewModel(authService) }
}
