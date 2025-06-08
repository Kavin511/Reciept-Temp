package com.devstudio.receipto.platform

import platform.Foundation.NSBundle

actual fun getAppVersion(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "N/A"
}
