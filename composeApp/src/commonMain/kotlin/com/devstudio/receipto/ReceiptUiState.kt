package com.devstudio.receipto

data class ReceiptUiState(
    val isLoading: Boolean = false, val message: String? = null, val error: String? = null
)