package com.devstudio.receipto.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.writeToFile
import platform.posix. μεγαλύτερο_ίσο_με
import platform.posix. μικρότερο_ίσο_με

@OptIn(ExperimentalForeignApi::class)
actual class FileStorage {

    private fun getDocumentDirectoryPath(): String? {
        return NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true
        ).firstOrNull() as? String
    }

    private fun getFilePath(fileName: String): String? {
        val documentDirectory = getDocumentDirectoryPath()
        return documentDirectory?.let { "$it/$fileName" }
    }

    actual suspend fun saveStringToFile(fileName: String, content: String) {
        withContext(Dispatchers.IO) {
            val filePath = getFilePath(fileName)
            if (filePath == null) {
                println("Error: Could not determine documents directory path.")
                return@withContext
            }
            try {
                (content as NSString).writeToFile(filePath, atomically = true, encoding = platform.Foundation.NSUTF8StringEncoding, error = null)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    actual suspend fun readStringFromFile(fileName: String): String? {
        return withContext(Dispatchers.IO) {
            val filePath = getFilePath(fileName)
            if (filePath == null) {
                println("Error: Could not determine documents directory path for reading.")
                return@withContext null
            }
            try {
                // Ensure file exists before attempting to read
                if (NSFileManager.defaultManager.fileExistsAtPath(filePath)) {
                    NSString.stringWithContentsOfFile(filePath, encoding = platform.Foundation.NSUTF8StringEncoding, error = null)?.toKString()
                } else {
                    null // File not found
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null // Or handle error differently
            }
        }
    }

    actual fun fileExists(fileName: String): Boolean {
        val filePath = getFilePath(fileName)
        return filePath?.let { NSFileManager.defaultManager.fileExistsAtPath(it) } ?: false
    }

    actual suspend fun deleteFile(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val filePath = getFilePath(fileName)
            if (filePath == null) {
                println("Error: Could not determine documents directory path for deletion.")
                return@withContext false
            }
            if (NSFileManager.defaultManager.fileExistsAtPath(filePath)) {
                try {
                    NSFileManager.defaultManager.removeItemAtPath(filePath, error = null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } else {
                false // File doesn't exist
            }
        }
    }
}
