plugins { id("com.android.application") }

dependencies { testImplementation("junit:junit:4.13.2") }

android {
    namespace = "com.privacydecoy.externalvpnfixture"
    compileSdk = 37
    defaultConfig {
        applicationId = providers.gradleProperty("fixtureApplicationId").getOrElse("com.privacydecoy.externalvpnfixture")
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "pr5-test-only"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
