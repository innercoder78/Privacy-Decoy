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
    // AG-1B debug stays unchanged; only this separately analyzed variant has a loader probe.
    buildTypes {
        create("dynamic") {
            initWith(getByName("debug"))
            matchingFallbacks += listOf("debug")
        }
    }
}
