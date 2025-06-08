package com.devstudio.receipto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gitlive.firebase.storage.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.russhwolf.settings.Settings
import com.devstudio.receipto.presentation.settings.currency.CurrencySelectionViewModel // For SELECTED_CURRENCY_CODE_KEY
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ReceiptViewModel(
    private val repository: ReceiptRepository, // Assuming ReceiptRepository is also injected/provided
    private val settings: Settings // Added Settings dependency
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiptUiState())
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State for the receipt being added/edited
    private val _currentReceipt = MutableStateFlow<Receipt?>(null)
    val currentReceipt: StateFlow<Receipt?> = _currentReceipt.asStateFlow()

    val receipts = repository.getReceiptsFlow().combine(searchQuery) { receipts, query ->
        if (query.isEmpty()) {
            receipts
        } else {
            receipts.filter { receipt ->
                receipt.name.contains(
                    query, ignoreCase = true
                ) || receipt.reason.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    @OptIn(ExperimentalUuidApi::class)
    fun loadReceipt(receiptId: String?) {
        viewModelScope.launch {
            if (receiptId == null) { // Creating a new receipt
                val uid = Uuid.random().toString()
                val defaultCurrencyCode = settings.getStringOrNull(CurrencySelectionViewModel.SELECTED_CURRENCY_CODE_KEY)
                _currentReceipt.value = Receipt(id = uid, currencyCode = defaultCurrencyCode) // Set currencyCode
            } else {
                // Try to find existing receipt, or initialize new if not found (error state)
                // When loading an existing receipt, its currencyCode should already be set.
                _currentReceipt.value = receipts.value.find { it.id == receiptId } ?: Receipt(
                    id = Uuid.random().toString()
                ) // Fallback to new if not found
            }
        }
    }

    fun addReceipt(receipt: Receipt, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.addReceipt(receipt).fold(onSuccess = {
                _uiState.update {
                    it.copy(
                        isLoading = false, message = "Receipt added successfully"
                    )
                }
                onSuccess() // Call onSuccess
            }, onFailure = { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            })
        }
    }

    fun updateReceipt(receipt: Receipt, onSuccess: () -> Unit) { // Add onSuccess
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.updateReceipt(receipt).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, message = "Receipt updated successfully")
                    }
                    onSuccess() // Call onSuccess
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun deleteReceipt(receiptId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.deleteReceipt(receiptId).fold(onSuccess = {
                _uiState.update {
                    it.copy(
                        isLoading = false, message = "Receipt deleted successfully"
                    )
                }
                onSuccess() // Call onSuccess
            }, onFailure = { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            })
        }
    }

    fun uploadImage(imageBytes: ByteArray, onSuccess: (String) -> Unit) { // Changed parameter
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Ensure repository.uploadImage can handle ByteArray
            repository.uploadImage(imageBytes).fold( // Pass ByteArray
                onSuccess = { url ->
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(url)
                }, onFailure = { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            })
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null, error = null) }
    }
}