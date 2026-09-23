pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PrivacyDecoy"
include(":app")
include(":probe-app", ":research-native")
include(":test-apps:external-vpn-fixture")
include(":test-apps:managed-profile-controller", ":test-apps:managed-profile-probe")
include(":test-apps:ag1-java-fixture", ":test-apps:ag1-dynamic-fixture")
include(":test-apps:ag1-precode-fixture")
