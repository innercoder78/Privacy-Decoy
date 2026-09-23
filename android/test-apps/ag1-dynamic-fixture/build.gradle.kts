plugins { id("com.android.application") }

android {
    namespace = "com.privacydecoy.ag1.dynamicfixture"
    compileSdk = 37
    defaultConfig {
        applicationId = "com.privacydecoy.ag1.dynamicfixture"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "ag1a-controlled"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
