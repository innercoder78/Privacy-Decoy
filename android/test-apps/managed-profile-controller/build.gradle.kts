plugins { id("com.android.application") }
android {
    namespace = "com.privacydecoy.research.profilecontroller"
    compileSdk = 37
    defaultConfig { applicationId = "com.privacydecoy.research.profilecontroller"; minSdk = 31; targetSdk = 37; versionCode = 1; versionName = "pr8-research" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
