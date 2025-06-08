package com.devstudio.receipto.remote

import com.devstudio.receipto.data.RestCountry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiClient {
    private val client = HttpClient { // Platform-specific engine will be chosen by Ktor
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true // Optional: for easier debugging of JSON
            })
        }
    }

    private val apiUrl = "https://restcountries.com/v3.1/all?fields=currencies,cca2,flag"

    suspend fun fetchCurrencyData(): List<RestCountry> {
        return try {
            client.get(apiUrl).body()
        } catch (e: Exception) {
            // TODO: Proper error handling (e.g., logging, returning Result object)
            println("Error fetching currency data: ${e.message}")
            emptyList()
        }
    }
}
