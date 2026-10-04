plugins { id("com.android.application") }

android {
    namespace = "com.privacydecoy.fixtures.earlyinit"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.privacydecoy.fixtures.earlyinit"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "phase-ii-fixture"
        multiDexEnabled = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Default fixture stays unchanged; only this separately analyzed variant has a loader probe.
    buildTypes {
        create("dynamic") {
            initWith(getByName("debug"))
            matchingFallbacks += listOf("debug")
        }
    }
}
