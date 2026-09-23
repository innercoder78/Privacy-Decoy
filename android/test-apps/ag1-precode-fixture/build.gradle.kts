plugins { id("com.android.application") }

android {
    namespace = "com.privacydecoy.ag1.precode"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.privacydecoy.ag1.precode"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "ag1b-controlled"
        multiDexEnabled = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
