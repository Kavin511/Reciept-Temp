package com.devstudio.receipto.core

import androidx.compose.runtime.Composable

/**
 * A Composable function that provides a launcher for picking an image from the device's gallery.
 *
 * @param onResult A callback function that will be invoked with an [ImagePickerResult]
 *                 containing the result of the image picking operation.
 * @return A lambda function that, when called, will launch the image picker.
 */
@Composable
actual fun rememberImagePickerLauncher(onResult: (ImagePickerResult) -> Unit): () -> Unit {
    TODO("Not yet implemented")
}