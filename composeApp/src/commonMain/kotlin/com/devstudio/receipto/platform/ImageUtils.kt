package com.devstudio.receipto.platform

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Converts a ByteArray to an ImageBitmap.
 * Actual implementations will handle platform-specific conversions.
 */
expect fun platformByteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap
