@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "app.judo.example"
        minSdkVersion(19)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
    }
    sourceSets.getByName("main") {
        java.srcDir("src/main/java")
        java.srcDir("src/main/kotlin")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.30")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.android.support:multidex:1.0.3")
    implementation(project(":sdk"))
    implementation("com.google.firebase:firebase-messaging-ktx:21.0.1")
    implementation("com.google.firebase:firebase-analytics-ktx:18.0.2")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.work:work-runtime:2.5.0")
}