plugins {
    id("com.android.application")
}

android {
    namespace = "com.privacydecoy.app"
    compileSdk = 37
    ndkVersion = "27.2.12479018"

    defaultConfig {
        applicationId = "com.privacydecoy.app"
        // Initial build baseline only; PR 3 will investigate supported platforms.
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0-dev"
        testInstrumentationRunner = "com.privacydecoy.research.PrototypeTestRunner"
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
    debugImplementation(project(":research-native"))
    testImplementation("junit:junit:4.13.2")
}

// Fixture stays an unchanged APK; only the test infrastructure extracts its DEX.
abstract class GenerateProbeAssets : DefaultTask() {
    @get:InputFile abstract val apk: RegularFileProperty
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

    @TaskAction fun generate() {
        val output = outputDirectory.get().asFile
        output.mkdirs()
        apk.get().asFile.copyTo(output.resolve("probe-app-debug.apk"), overwrite = true)
    }
}
val generateProbeAssets = tasks.register<GenerateProbeAssets>("generateProbeAssets") {
    dependsOn(":probe-app:assembleDebug")
    apk.set(project(":probe-app").layout.buildDirectory.file("outputs/apk/debug/probe-app-debug.apk"))
    outputDirectory.set(layout.buildDirectory.dir("generated/probeAssets"))
}
androidComponents.onVariants(androidComponents.selector().withBuildType("debug")) { variant ->
    variant.androidTest?.sources?.assets?.addGeneratedSourceDirectory(generateProbeAssets, GenerateProbeAssets::outputDirectory)
}
