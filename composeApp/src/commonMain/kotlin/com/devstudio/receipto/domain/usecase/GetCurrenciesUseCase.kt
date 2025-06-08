package com.devstudio.receipto.domain.usecase

import com.devstudio.receipto.domain.model.Currency
import com.devstudio.receipto.domain.repository.CurrencyRepository
import com.devstudio.receipto.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCurrenciesUseCase(private val repository: CurrencyRepository) {
    operator fun invoke(): Flow<Resource<List<Currency>>> = flow {
        emit(Resource.Loading()) // Emit loading state

        // The repository's getCurrencies() method is expected to handle
        // the logic of fetching from cache and syncing if necessary.
        val result = repository.getCurrencies()
        emit(result)

        // Alternative approach (explicit sync):
        // This might be preferred if the UI needs to trigger sync independently
        // or if getCurrencies() is strictly for cache access.
        // For now, assuming getCurrencies() is smart enough.

        // emit(Resource.Loading()) // Initial loading state
        // val localData = repository.getLocalCurrencies() // Assuming a method to get only local
        // if (localData.data.isNullOrEmpty()) {
        //     val syncResult = repository.syncRemoteCurrencies()
        //     if (syncResult is Resource.Success) {
        //         emit(repository.getCurrencies()) // Fetch again after sync
        //     } else if (syncResult is Resource.Error) {
        //         emit(Resource.Error(syncResult.message ?: "Failed to sync and fetch currencies"))
        //     }
        // } else {
        //     emit(localData) // Emit local data if available
        //     // Optionally, trigger a background sync here if data is stale
        //     // val syncResult = repository.syncRemoteCurrencies()
        //     // if (syncResult is Resource.Success) {
        //     //    emit(repository.getCurrencies()) // Emit updated data
        //     // }
        // }
    }
}
