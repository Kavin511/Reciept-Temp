package com.devstudio.receipto.presentation.receipt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.devstudio.receipto.ReceiptRepository // Domain or data layer repository
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import com.devstudio.receipto.ReceiptViewModel
import platform.Foundation.NSUserDefaults

@Composable
actual fun rememberReceiptViewModel(): ReceiptViewModel {
    // Assuming existing ReceiptRepository() default constructor is acceptable for now
    val receiptRepository = remember { com.devstudio.receipto.ReceiptRepository() } // Data layer one

    val settings: Settings = remember {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }

    return remember { ReceiptViewModel(receiptRepository, settings) }
}
