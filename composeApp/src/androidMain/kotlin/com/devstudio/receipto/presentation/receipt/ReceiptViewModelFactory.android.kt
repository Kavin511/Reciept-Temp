package com.devstudio.receipto.presentation.receipt

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.devstudio.receipto.ReceiptRepository // Domain repository, if used, or data layer directly
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.devstudio.receipto.ReceiptViewModel

// Assuming ReceiptRepository is now an interface and we need its Impl
// For this example, let's assume the existing ReceiptRepository class is the Impl
// and it doesn't have further complex dependencies for now, or they are default.
// If ReceiptRepository itself has complex deps like Firebase instances, they'd be created here.

@Composable
actual fun rememberReceiptViewModel(): ReceiptViewModel {
    val context = LocalContext.current.applicationContext

    // Assuming existing ReceiptRepository() default constructor is acceptable for now
    // or it would be constructed with its own dependencies (e.g. Firebase instances)
    val receiptRepository = remember { com.devstudio.receipto.ReceiptRepository() } // The one from data layer

    val settings: Settings = remember {
        SharedPreferencesSettings(context.getSharedPreferences("app_settings", Context.MODE_PRIVATE))
    }

    return remember { ReceiptViewModel(receiptRepository, settings) }
}
