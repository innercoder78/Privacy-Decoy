plugins {
    id("com.android.application")
}

android {
    namespace = "com.privacydecoy.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.privacydecoy.app"
        // Initial build baseline only; PR 2 will investigate supported platforms.
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0-dev"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = true
    }

    testOptions {
        unitTests.all {
            it.testLogging {
                events("failed", "skipped")
            }
        }
    }
}

// AGP 9 provides built-in Kotlin; no separate Kotlin Android plugin is needed.
dependencies {
    testImplementation("junit:junit:4.13.2")
}
