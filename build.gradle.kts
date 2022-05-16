// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val judoVersion by extra("1.8.1-beta1")
    val composeVersion by extra("1.0.1")

    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("com.google.gms:google-services:4.3.5")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = java.net.URI.create("https://judoapp.github.io/judo-android-libs/maven")
        }
    }
}
