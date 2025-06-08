package com.devstudio.receipto.presentation.settings.currency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.devstudio.receipto.data.FileStorage
import com.devstudio.receipto.data.JsonProvider
import com.devstudio.receipto.data.datasource.CurrencyLocalDataSource
import com.devstudio.receipto.data.repository.CurrencyRepositoryImpl
import com.devstudio.receipto.domain.usecase.GetCurrenciesUseCase
import com.devstudio.receipto.remote.CurrencyApiClient
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings // For Android actual of Settings

@Composable
actual fun rememberCurrencySelectionViewModel(): CurrencySelectionViewModel {
    val context = LocalContext.current.applicationContext

    // Create dependencies. In a real app, use DI (e.g., Koin, Hilt).
    val fileStorage = remember { FileStorage(context) }
    val json = remember { JsonProvider.json } // Using the shared JsonProvider
    val apiClient = remember { CurrencyApiClient() } // Assumes no context needed or handles it internally
    val localDataSource = remember { CurrencyLocalDataSource(fileStorage, json) }
    val currencyRepository = remember { CurrencyRepositoryImpl(apiClient, localDataSource) }
    val getCurrenciesUseCase = remember { GetCurrenciesUseCase(currencyRepository) }

    // For multiplatform-settings, Android needs a SharedPreferences instance.
    // The 'no-arg' variant of Settings() often creates a default instance.
    // If specific SharedPreferences are needed, a SettingsFactory can be used.
    // val settings: Settings = remember { Settings() } // Simplest way for no-arg
    // Or, more explicitly for Android if you named your prefs file:
    val settings: Settings = remember { SharedPreferencesSettings(context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)) }


    return remember { CurrencySelectionViewModel(getCurrenciesUseCase, settings) }
}
