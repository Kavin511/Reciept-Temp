package com.devstudio.receipto.presentation.settings.currency

import com.devstudio.receipto.domain.model.Currency
import com.devstudio.receipto.domain.usecase.GetCurrenciesUseCase
import com.devstudio.receipto.util.Resource
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.coroutines.putStringFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrencySelectionViewModel(
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val settings: Settings, // From multiplatform-settings
    private val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
) {
    private val _uiState = MutableStateFlow(CurrencyScreenState())
    val uiState: StateFlow<CurrencyScreenState> = _uiState.asStateFlow()

    companion object {
        const val SELECTED_CURRENCY_CODE_KEY = "selected_currency_code"
    }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Observe selected currency changes from settings
            settings.getStringOrNullFlow(SELECTED_CURRENCY_CODE_KEY)
                .onEach { code ->
                    val currentLoadedCurrencies = _uiState.value.currencies
                    if (currentLoadedCurrencies.isNotEmpty()) {
                        _uiState.update {
                            it.copy(selectedCurrency = currentLoadedCurrencies.find { c -> c.code == code })
                        }
                    }
                }.launchIn(viewModelScope)


            // Fetch all currencies
            getCurrenciesUseCase().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val allCurrencies = result.data ?: emptyList()
                        val previouslySelectedCode = settings.getStringOrNull(SELECTED_CURRENCY_CODE_KEY)
                        _uiState.update {
                            it.copy(
                                currencies = allCurrencies,
                                filteredCurrencies = filterCurrencies(allCurrencies, it.searchQuery),
                                selectedCurrency = allCurrencies.find { c -> c.code == previouslySelectedCode },
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredCurrencies = filterCurrencies(it.currencies, query)
            )
        }
    }

    private fun filterCurrencies(currencies: List<Currency>, query: String): List<Currency> {
        if (query.isBlank()) {
            return currencies
        }
        return currencies.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.code.contains(query, ignoreCase = true) ||
            (it.symbol.contains(query, ignoreCase = true))
        }
    }

    fun onCurrencySelected(currency: Currency) {
        viewModelScope.launch {
            settings.putString(SELECTED_CURRENCY_CODE_KEY, currency.code)
            // The getStringOrNullFlow observer will update the selectedCurrency in uiState
            // Or, update it immediately:
            _uiState.update { it.copy(selectedCurrency = currency) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onClear() {
        viewModelScope.cancel()
    }
}
