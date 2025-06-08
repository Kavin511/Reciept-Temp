package com.devstudio.receipto.data.repository

import com.devstudio.receipto.data.datasource.CurrencyLocalDataSource
import com.devstudio.receipto.domain.model.Currency
import com.devstudio.receipto.domain.repository.CurrencyRepository
import com.devstudio.receipto.remote.CurrencyApiClient
import com.devstudio.receipto.util.Resource

class CurrencyRepositoryImpl(
    private val apiClient: CurrencyApiClient,
    private val localDataSource: CurrencyLocalDataSource
) : CurrencyRepository {

    override suspend fun getCurrencies(): Resource<List<Currency>> {
        return if (localDataSource.hasCachedCurrencies()) {
            val cachedCurrencies = localDataSource.loadCurrencies()
            if (cachedCurrencies != null) {
                Resource.Success(cachedCurrencies)
            } else {
                // Cache exists but loading failed (e.g., corrupted file)
                // Attempt to sync and overwrite cache
                val syncResult = syncRemoteCurrencies()
                if (syncResult is Resource.Success) {
                    val newCachedCurrencies = localDataSource.loadCurrencies()
                    if (newCachedCurrencies != null) {
                        Resource.Success(newCachedCurrencies)
                    } else {
                        Resource.Error("Failed to load currencies after sync following cache error.")
                    }
                } else {
                    Resource.Error(syncResult.message ?: "Failed to sync currencies after cache error.")
                }
            }
        } else {
            // No cache, sync from remote
            val syncResult = syncRemoteCurrencies()
            if (syncResult is Resource.Success) {
                val newCachedCurrencies = localDataSource.loadCurrencies()
                if (newCachedCurrencies != null) {
                    Resource.Success(newCachedCurrencies)
                } else {
                    Resource.Error("Failed to load currencies after initial sync.")
                }
            } else {
                Resource.Error(syncResult.message ?: "Failed to fetch currencies.")
            }
        }
    }

    override suspend fun syncRemoteCurrencies(): Resource<Unit> {
        return try {
            val apiResponse = apiClient.fetchCurrencyData() // This is List<RestCountry>
            if (apiResponse.isEmpty() && apiClient.fetchCurrencyData().isNotEmpty()){ // Basic check if the API might be temporarily empty vs consistently empty
                 // This condition might indicate an issue with the API returning empty when it shouldn't
                 // However, an empty list from API could also be valid in some edge cases.
                 // For now, we'll proceed if it's not empty. If it is, we might not want to overwrite a good cache.
                 // But if there's no cache, an empty list means we save an empty list.
            }

            val domainCurrencies = transformToDomainModel(apiResponse)
            localDataSource.saveCurrencies(domainCurrencies)
            Resource.Success(Unit)
        } catch (e: Exception) {
            // TODO: More specific error logging/handling
            Resource.Error("Failed to sync remote currencies: ${e.message}")
        }
    }

    private fun transformToDomainModel(apiCountries: List<com.devstudio.receipto.data.RestCountry>): List<Currency> {
        val uniqueCurrenciesMap = mutableMapOf<String, Currency>()
        apiCountries.forEach { country ->
            country.currencies?.forEach { (code, detail) ->
                if (!uniqueCurrenciesMap.containsKey(code)) { // Ensure uniqueness by currency code
                    if (code.isNotBlank() && detail.name.isNotBlank()) { // Filter out entries without code or name
                        uniqueCurrenciesMap[code] = Currency(
                            code = code,
                            name = detail.name,
                            symbol = detail.symbol ?: "", // Default to empty if symbol is null
                            flagEmoji = country.flag ?: "" // Use country flag, default to empty
                        )
                    }
                }
            }
        }
        return uniqueCurrenciesMap.values.toList().sortedBy { it.name } // Sort alphabetically by name
    }
}
