pluginManagement { repositories { google(); mavenCentral() } }
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "PrivacyDecoy"
include(":app")
include(":probe-app", ":research-native")
include(":test-apps:external-vpn-fixture")
include(":test-apps:java-surface-fixture", ":test-apps:dynamic-code-surface-fixture")
include(":test-apps:early-init-loader-fixture", ":test-apps:secondary-dex-fixture")
