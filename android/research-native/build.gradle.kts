plugins { id("com.android.library") }
android {
    namespace = "com.privacydecoy.research.nativeprobe"
    compileSdk = 37
    ndkVersion = "27.2.12479018"
    defaultConfig {
        minSdk = 31
        ndk { abiFilters += listOf("x86_64", "arm64-v8a") }
    }
    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt"); version = "3.22.1" } }
}
