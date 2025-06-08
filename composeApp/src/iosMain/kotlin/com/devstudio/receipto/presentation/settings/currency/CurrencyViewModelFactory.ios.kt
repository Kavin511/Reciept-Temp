package com.devstudio.receipto.presentation.settings.currency

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.devstudio.receipto.data.FileStorage
import com.devstudio.receipto.data.JsonProvider
import com.devstudio.receipto.data.datasource.CurrencyLocalDataSource
import com.devstudio.receipto.data.repository.CurrencyRepositoryImpl
import com.devstudio.receipto.domain.usecase.GetCurrenciesUseCase
import com.devstudio.receipto.remote.CurrencyApiClient
import com.russhwolf.settings.NSUserDefaultsSettings // For iOS actual of Settings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults // For iOS

@Composable
actual fun rememberCurrencySelectionViewModel(): CurrencySelectionViewModel {
    // Create dependencies. In a real app, use DI.
    val fileStorage = remember { FileStorage() } // iOS FileStorage doesn't need context in constructor
    val json = remember { JsonProvider.json }
    val apiClient = remember { CurrencyApiClient() }
    val localDataSource = remember { CurrencyLocalDataSource(fileStorage, json) }
    val currencyRepository = remember { CurrencyRepositoryImpl(apiClient, localDataSource) }
    val getCurrenciesUseCase = remember { GetCurrenciesUseCase(currencyRepository) }

    // For multiplatform-settings, iOS uses NSUserDefaults.
    // val settings: Settings = remember { Settings() } // Simplest way for no-arg
    // Or, more explicitly for iOS:
    val settings: Settings = remember { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }


    return remember { CurrencySelectionViewModel(getCurrenciesUseCase, settings) }
}
