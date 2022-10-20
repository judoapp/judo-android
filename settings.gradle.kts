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
            url = java.net.URI.create("https://judoapp.github.io/judo-maven/maven")
        }
    }
}

include(":example", ":sdk", ":judo-compose")
rootProject.name = "Judo Android"
include(":testbench")
