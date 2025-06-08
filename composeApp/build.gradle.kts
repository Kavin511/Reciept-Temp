import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "1.9.0" // This lines
    id("com.google.gms.google-services") version "4.4.1"
    // alias(libs.plugins.sqldelight) // Removed SQLDelight plugin
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            api(platform("com.google.firebase:firebase-bom:33.15.0")) // This line to add the firebase bom
            implementation("com.google.firebase:firebase-auth-ktx") // Added Firebase Auth KTX
            implementation(libs.play.services.auth) // Added Play Services Auth for Google Sign-In
            // implementation(libs.sqldelight.android.driver) // Removed SQLDelight Android driver
            implementation(libs.ktor.client.okhttp) // Added Ktor OkHttp engine for Android
        }
        iosMain.dependencies { // Assuming iosMain exists for SQLDelight native driver
            // implementation(libs.sqldelight.native.driver) // Removed SQLDelight Native driver
            implementation(libs.ktor.client.darwin) // Added Ktor Darwin engine for iOS
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            // implementation(libs.sqldelight.runtime) // Removed SQLDelight runtime
            implementation(libs.ktor.client.core) // Added Ktor client core
            implementation(libs.ktor.client.content.negotiation) // Added Ktor content negotiation
            implementation(libs.ktor.serialization.kotlinx.json) // Added Ktor Kotlinx JSON serialization
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.components.resources)
            implementation(libs.firebase.firestore) // This line
            implementation(libs.firebase.common)// This line
            implementation(libs.firebase.storage) // This line
            implementation(libs.firebase.auth) // Added GitLive Firebase Auth for commonMain
            implementation(libs.material.icons.extended)
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta01")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0") // This line
            implementation("com.google.firebase:firebase-common-ktx:20.3.3")
            implementation("io.coil-kt:coil-compose:2.6.0")
            implementation(libs.multiplatform.settings.no.arg) // Added multiplatform-settings
            // implementation(libs.voyager.navigator) // Removed Voyager Navigator
            // implementation(libs.voyager.transitions) // Removed Voyager Transitions

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.devstudio.receipto"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.devstudio.receipto"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Removed SQLDelight configuration block
// sqldelight {
//    databases {
//        create("AppDatabase") {
//            packageName.set("com.devstudio.receipto.db")
//        }
//    }
// }
