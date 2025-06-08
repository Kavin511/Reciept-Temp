package com.devstudio.receipto.domain.model

// This is the clean model for the UI/Domain layer
data class Currency(
    val code: String,        // e.g., "USD"
    val name: String,        // e.g., "United States Dollar"
    val symbol: String,      // e.g., "$"
    val flagEmoji: String?   // e.g., "🇺🇸"
)
