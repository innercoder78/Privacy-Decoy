plugins { id("com.android.application") }

android {
    namespace = "com.privacydecoy.probe"
    compileSdk = 37
    ndkVersion = "27.2.12479018"
    defaultConfig {
        applicationId = "com.privacydecoy.probe"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "research-only"
        // This deliberately small experiment transfers only classes.dex.
        multiDexEnabled = false
        ndk { abiFilters += listOf("x86_64", "arm64-v8a") }
    }
    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt"); version = "3.22.1" } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
