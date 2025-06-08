package com.devstudio.receipto.data

expect class FileStorage {
    suspend fun saveStringToFile(fileName: String, content: String)
    suspend fun readStringFromFile(fileName: String): String?
    fun fileExists(fileName: String): Boolean
    suspend fun deleteFile(fileName: String): Boolean
}
