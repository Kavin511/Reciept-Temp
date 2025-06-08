package com.devstudio.receipto.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

actual class FileStorage(private val context: Context) {
    actual suspend fun saveStringToFile(fileName: String, content: String) {
        withContext(Dispatchers.IO) {
            try {
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(content.toByteArray())
                }
            } catch (e: IOException) {
                // Handle error, e.g., log it or throw a custom exception
                e.printStackTrace()
            }
        }
    }

    actual suspend fun readStringFromFile(fileName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                context.openFileInput(fileName).bufferedReader().useLines { lines ->
                    lines.joinToString(separator = "\n")
                }
            } catch (e: FileNotFoundException) {
                null // File not found is a valid case
            } catch (e: IOException) {
                e.printStackTrace()
                null // Or handle error differently
            }
        }
    }

    actual fun fileExists(fileName: String): Boolean {
        val file = context.getFileStreamPath(fileName)
        return file != null && file.exists()
    }

    actual suspend fun deleteFile(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = context.getFileStreamPath(fileName)
                if (file != null && file.exists()) {
                    file.delete()
                } else {
                    false // File doesn't exist or path is invalid
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                false
            }
        }
    }
}
