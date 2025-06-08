package com.devstudio.receipto.platform

import android.content.Context
import android.content.pm.PackageManager
import com.devstudio.receipto.MainActivity // Assuming MainActivity might hold context or be the app class

// To make this work correctly without passing context directly to getAppVersion(),
// the Android application needs a way to provide its context statically or via a DI framework.
// For instance, if your Application class stores a static reference to itself:
// class MainApplication : Application() {
//     companion object {
//         lateinit var instance: MainApplication
//             private set
//     }
//     override fun onCreate() {
//         super.onCreate()
//         instance = this
//     }
// }
// Then you could use MainApplication.instance.applicationContext here.
// Lacking such setup, this actual function cannot directly get the version.
// The call in common code `getAppVersion()` will hit this.
// For this exercise, we'll return a placeholder.
// In a real app, SettingsScreenViewModel (Android specific part) would get context
// and call a version of getAppVersion(context: Context).

// A simplified approach for this task: Assume a global context provider exists.
// THIS IS A SIMPLIFICATION and assumes `MyApp.context` is set somewhere, e.g. Application class.
object AndroidAppContextProvider {
    var applicationContext: Context? = null
}

actual fun getAppVersion(): String {
    val context = AndroidAppContextProvider.applicationContext
    if (context == null) {
        return "N/A (Context not available)" // Or a default/error string
    }
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "N/A (No version name)"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A (Package not found)"
    } catch (e: Exception) {
        "N/A (Error fetching)"
    }
}
