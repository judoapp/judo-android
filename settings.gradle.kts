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
        // since we are (temporarily) now shipping the judo-compose library directly
        // in the sdk/maven subdir maven repo in this git repo directly (until judo-maven comes
        // online and customers are instructed to move to it), to solve the chicken-and-egg
        // problem that emerges when trying to build judo-android against the built copy
        // of judo-compose that has been delivered into that subdir, we have to add our own
        // maven subdir as a repository.
        maven {
            url = File(settingsDir, "sdk/maven").toURI()
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
