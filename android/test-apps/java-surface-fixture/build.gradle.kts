plugins { id("com.android.application") }

android {
    namespace = "com.privacydecoy.fixtures.javasurface"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.privacydecoy.fixtures.javasurface"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "phase-ii-fixture"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
