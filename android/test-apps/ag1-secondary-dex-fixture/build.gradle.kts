plugins { id("com.android.application") }
android {
    namespace = "com.privacydecoy.ag1.secondary"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.privacydecoy.ag1.secondary"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "ag1c-synthetic"
        multiDexEnabled = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
