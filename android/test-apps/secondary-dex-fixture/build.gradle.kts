plugins { id("com.android.application") }
android {
    namespace = "com.privacydecoy.fixtures.secondarydex"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.privacydecoy.fixtures.secondarydex"
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
}
