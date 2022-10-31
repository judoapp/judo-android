// Top-level build file where you can add configuration options common to all sub-projects/modules.

val judoVersion by extra("1.13.5")
val judoComposeVersion by extra("1.0.6")

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.1.1" apply false
    id("com.android.library") version "7.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
}

task("printVersionNumber") {
    doLast {
        println("Judo Android SDK version $judoVersion")
        // GitHub Actions detects this syntax on stdout and sets an output variable (`VERSION` in this case)
        // that we can use later on within the workflow.

        // Groovy: println "::set-output name=VERSION::${judoVersion}"
        println("::set-output name=VERSION::$judoVersion")
    }
}
