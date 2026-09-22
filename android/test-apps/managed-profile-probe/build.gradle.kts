plugins { id("com.android.application") }
android {
    namespace = "com.privacydecoy.research.managedprobe"
    compileSdk = 37
    ndkVersion = "27.2.12479018"
    defaultConfig {
        minSdk = 31; targetSdk = 37; versionCode = 1; versionName = "pr8-research"
        externalNativeBuild { cmake { cppFlags += "-std=c++17" } }
    }
    flavorDimensions += "tenant"
    productFlavors {
        create("tenantA") { dimension = "tenant"; applicationId = "com.privacydecoy.research.managedprobe.a" }
        create("tenantB") { dimension = "tenant"; applicationId = "com.privacydecoy.research.managedprobe.b" }
    }
    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt"); version = "3.22.1" } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
