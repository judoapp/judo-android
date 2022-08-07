pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = java.net.URI.create("https://judoapp.github.io/judo-android-libs/maven")
        }
        maven {
            url = java.net.URI.create("https://judoapp.github.io/judo-compose/sdk/maven")
        }
    }
}

include(":example", ":sdk", ":judo-compose")
rootProject.name = "Judo Android"
include(":testbench")

if (File("../judo-compose-develop/sdk").exists()) {
    // when developers are working on their local workstations, we use a sibling directory with the SDK.
    project(":judo-compose").projectDir = File(settingsDir, "../judo-compose-develop/sdk")
}