package com.devstudio.receipto.core

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.devstudio.receipto.BuildConfig // Assuming BuildConfig is available for applicationId
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual class ImagePickerResult(
    actual val uri: Uri?, // android.net.Uri
    actual val byteArray: ByteArray?,
    actual val error: String?
)

@Composable
actual fun rememberImagePickerLauncher(onResult: (ImagePickerResult) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val byteArray = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (byteArray != null) {
                    onResult(ImagePickerResult(uri = uri, byteArray = byteArray, error = null))
                } else {
                    onResult(ImagePickerResult(uri = uri, byteArray = null, error = "Failed to read image bytes."))
                }
            } catch (e: Exception) {
                onResult(ImagePickerResult(uri = uri, byteArray = null, error = e.message ?: "Error reading image"))
            }
        } else {
            // User cancelled picker
            onResult(ImagePickerResult(uri = null, byteArray = null, error = null))
        }
    }
    return remember { { launcher.launch("image/*") } }
}

@Composable
actual fun rememberCameraLauncher(onResult: (ImagePickerResult) -> Unit): () -> Unit {
    val context = LocalContext.current
    var tempUri: Uri? = null

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempUri != null) {
            try {
                val byteArray = context.contentResolver.openInputStream(tempUri!!)?.use { it.readBytes() }
                if (byteArray != null) {
                    onResult(ImagePickerResult(uri = tempUri, byteArray = byteArray, error = null))
                } else {
                    onResult(ImagePickerResult(uri = tempUri, byteArray = null, error = "Failed to read image bytes from camera."))
                }
            } catch (e: Exception) {
                onResult(ImagePickerResult(uri = tempUri, byteArray = null, error = e.message ?: "Error reading camera image"))
            }
        } else if (tempUri == null && !success) {
             onResult(ImagePickerResult(uri = null, byteArray = null, error = "Camera action cancelled or failed before URI generation."))
        }
         else {
            // Handle failure or cancellation if tempUri was generated but taking picture failed.
            // If tempUri is null here, it means file creation failed before launching camera.
            onResult(ImagePickerResult(uri = tempUri, byteArray = null, error = "Camera capture failed or was cancelled."))
        }
    }

    return remember {
        {
            try {
                val photoFile: File = createImageFile(context)
                // Authority must match the one in AndroidManifest.xml and be dynamically fetched
                val authority = "${BuildConfig.APPLICATION_ID}.fileprovider"
                tempUri = FileProvider.getUriForFile(context, authority, photoFile)
                launcher.launch(tempUri)
            } catch (e: Exception) {
                onResult(ImagePickerResult(uri = null, byteArray = null, error = "Failed to create image file: ${e.message}"))
            }
        }
    }
}

private fun createImageFile(context: Context): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir: File? = context.getExternalFilesDir("images") // Using getExternalFilesDir for app-specific visible files or cacheDir for private
    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}
