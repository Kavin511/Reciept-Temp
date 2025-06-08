package com.devstudio.receipto.presentation.settings.currency
import com.devstudio.receipto.domain.model.Currency

data class CurrencyScreenState(
    val currencies: List<Currency> = emptyList(),
    val filteredCurrencies: List<Currency> = emptyList(),
    val selectedCurrency: Currency? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
