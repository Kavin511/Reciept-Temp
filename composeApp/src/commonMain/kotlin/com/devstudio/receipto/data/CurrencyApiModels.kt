package com.devstudio.receipto.data

import kotlinx.serialization.Serializable

@Serializable
data class RestCountry(
    val cca2: String, // Country code e.g., "US"
    val currencies: Map<String, CurrencyNameSymbol>? = null, // Key is currency code e.g., "USD"
    val flag: String? = null // Unicode flag emoji
)

@Serializable
data class CurrencyNameSymbol(
    val name: String,
    val symbol: String? = null
)
