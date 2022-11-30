val judoVersion by extra("1.8.4")
val composeVersion by extra("1.0.1")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("com.google.gms:google-services:4.3.14")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            url = java.net.URI.create("https://judoapp.github.io/judo-android-libs/maven")
        }
    }
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
