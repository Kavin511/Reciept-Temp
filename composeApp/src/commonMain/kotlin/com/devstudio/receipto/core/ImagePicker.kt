package com.devstudio.receipto.core

import androidx.compose.runtime.Composable

/**
 * A wrapper class for the result of an image picker operation.
 * It can contain a platform-specific URI, a ByteArray of the image data, or an error message.
 */
expect class ImagePickerResult {
    val uri: Any?
    val byteArray: ByteArray?
    val error: String?
}

/**
 * A Composable function that provides a launcher for picking an image from the device's gallery.
 *
 * @param onResult A callback function that will be invoked with an [ImagePickerResult]
 *                 containing the result of the image picking operation.
 * @return A lambda function that, when called, will launch the image picker.
 */
@Composable
expect fun rememberImagePickerLauncher(onResult: (ImagePickerResult) -> Unit): () -> Unit

/**
 * A Composable function that provides a launcher for capturing an image using the device's camera.
 *
 * @param onResult A callback function that will be invoked with an [ImagePickerResult]
 *                 containing the result of the camera capture operation.
 * @return A lambda function that, when called, will launch the camera.
 */
@Composable
expect fun rememberCameraLauncher(onResult: (ImagePickerResult) -> Unit): () -> Unit
