package com.devstudio.receipto.data.datasource

import com.devstudio.receipto.data.FileStorage
import com.devstudio.receipto.domain.model.Currency
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CurrencyLocalDataSource(
    private val fileStorage: FileStorage,
    private val json: Json
) {
    private val currenciesFileName = "currencies.json"

    suspend fun saveCurrencies(currencies: List<Currency>) {
        try {
            val jsonString = json.encodeToString(currencies)
            fileStorage.saveStringToFile(currenciesFileName, jsonString)
        } catch (e: Exception) {
            // TODO: Log error or handle more gracefully
            println("Error saving currencies to file: ${e.message}")
            // Optionally rethrow or return a result type
        }
    }

    suspend fun loadCurrencies(): List<Currency>? {
        if (!hasCachedCurrencies()) {
            return null
        }
        return try {
            val jsonString = fileStorage.readStringFromFile(currenciesFileName)
            jsonString?.let { json.decodeFromString<List<Currency>>(it) }
        } catch (e: Exception) {
            // TODO: Log error or handle more gracefully
            println("Error loading currencies from file: ${e.message}")
            // Optionally delete the corrupt file: fileStorage.deleteFile(currenciesFileName)
            null
        }
    }

    fun hasCachedCurrencies(): Boolean {
        return fileStorage.fileExists(currenciesFileName)
    }

    suspend fun deleteCache() {
        fileStorage.deleteFile(currenciesFileName)
    }
}
