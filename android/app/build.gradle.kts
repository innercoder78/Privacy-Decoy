import java.security.MessageDigest
import java.util.zip.ZipFile

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

abstract class GenerateAg1Assets : DefaultTask() {
    @get:InputFile abstract val apk: RegularFileProperty
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty
    @TaskAction fun generate() {
        val source = apk.get().asFile
        val output = outputDirectory.get().asFile
        output.mkdirs()
        val asset = output.resolve("ag1-precode-fixture-debug.apk")
        source.copyTo(asset, overwrite = true)
        val digest = MessageDigest.getInstance("SHA-256")
        check(digest.digest(source.readBytes()).contentEquals(digest.digest(asset.readBytes()))) {
            "AG-1 generated asset differs from source APK"
        }
    }
}
val generateAg1Assets = tasks.register<GenerateAg1Assets>("generateAg1Assets") {
    dependsOn(":test-apps:ag1-precode-fixture:assembleDebug")
    apk.set(project(":test-apps:ag1-precode-fixture").layout.buildDirectory.file("outputs/apk/debug/ag1-precode-fixture-debug.apk"))
    outputDirectory.set(layout.buildDirectory.dir("generated/ag1Assets"))
}
androidComponents.onVariants(androidComponents.selector().withBuildType("debug")) { variant ->
    variant.androidTest?.sources?.assets?.addGeneratedSourceDirectory(generateAg1Assets, GenerateAg1Assets::outputDirectory)
}

abstract class GenerateAg1DynamicAssets : DefaultTask() {
    @get:InputFile abstract val primary: RegularFileProperty
    @get:InputFile abstract val secondary: RegularFileProperty
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty
    @TaskAction fun generate() {
        val output = outputDirectory.get().asFile
        output.mkdirs()
        primary.get().asFile.copyTo(output.resolve("ag1-precode-fixture-dynamic.apk"), overwrite = true)
        ZipFile(secondary.get().asFile).use { zip ->
            zip.getInputStream(zip.getEntry("classes.dex")).use {
                output.resolve("ag1-secondary.dex").writeBytes(it.readBytes())
            }
        }
    }
}
val generateAg1DynamicAssets = tasks.register<GenerateAg1DynamicAssets>("generateAg1DynamicAssets") {
    dependsOn(":test-apps:ag1-precode-fixture:assembleDynamic", ":test-apps:ag1-secondary-dex-fixture:assembleDebug")
    primary.set(project(":test-apps:ag1-precode-fixture").layout.buildDirectory.file("outputs/apk/dynamic/ag1-precode-fixture-dynamic.apk"))
    secondary.set(project(":test-apps:ag1-secondary-dex-fixture").layout.buildDirectory.file("outputs/apk/debug/ag1-secondary-dex-fixture-debug.apk"))
    outputDirectory.set(layout.buildDirectory.dir("generated/ag1DynamicAssets"))
}
androidComponents.onVariants(androidComponents.selector().withBuildType("debug")) { variant ->
    variant.androidTest?.sources?.assets?.addGeneratedSourceDirectory(generateAg1DynamicAssets, GenerateAg1DynamicAssets::outputDirectory)
}
