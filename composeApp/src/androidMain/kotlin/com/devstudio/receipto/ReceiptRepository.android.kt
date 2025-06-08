package com.devstudio.receipto

import android.net.Uri
import dev.gitlive.firebase.storage.File
import java.io.FileOutputStream

actual fun createTempFileFromByteArray(fileByteArray: ByteArray): File {
    val tempFile = java.io.File.createTempFile("temp", null)

    FileOutputStream(tempFile).use { outputStream ->
        outputStream.write(fileByteArray)
    }

    return File(Uri.fromFile((tempFile)))
}