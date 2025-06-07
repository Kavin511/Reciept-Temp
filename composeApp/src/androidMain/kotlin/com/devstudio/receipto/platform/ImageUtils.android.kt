package com.devstudio.receipto.platform

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Android implementation for converting a ByteArray to an ImageBitmap.
 */
actual fun platformByteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size).asImageBitmap()
}
