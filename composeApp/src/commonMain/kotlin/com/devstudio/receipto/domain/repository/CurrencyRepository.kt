package com.devstudio.receipto.domain.repository

import com.devstudio.receipto.domain.model.Currency
import com.devstudio.receipto.util.Resource // A generic Resource wrapper

interface CurrencyRepository {
    // Fetches from local cache; initiates API fetch and save if cache is empty/stale
    suspend fun getCurrencies(): Resource<List<Currency>>

    // May not be needed if getCurrencies handles sync, but good for explicit refresh
    suspend fun syncRemoteCurrencies(): Resource<Unit>
}
