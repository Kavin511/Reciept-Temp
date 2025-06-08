package com.devstudio.receipto.data

import kotlinx.serialization.json.Json

object JsonProvider {
    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true // Good for debugging files; can be turned off for release
        encodeDefaults = true // Important if you want to ensure all fields are present in JSON
    }
}
