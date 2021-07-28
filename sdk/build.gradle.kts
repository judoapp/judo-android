@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-kapt")
    id("maven-publish")
}

val judoGroupId = "app.judo"
val judoArtifactId = "judo-sdk"
val judoVersion = "1.2.0"

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(19)
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
    }

    sourceSets.getByName("main") {
        java.srcDir("src/main/java")
        java.srcDir("src/main/kotlin")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // region Jetbrains
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3")
    // endregion Jetbrains

    // region Google
    implementation ("com.google.android.exoplayer:exoplayer-core:2.12.0")
    implementation ("com.google.android.exoplayer:exoplayer-hls:2.12.0")
    implementation ("com.google.android.exoplayer:exoplayer-dash:2.12.0")
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.12.0")
    // endregion Google

    // region AndroidX
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-process:2.3.1")
    implementation("androidx.browser:browser:1.2.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    // endregion AndroidX

    // region Coil
    implementation("io.coil-kt:coil-base:1.0.0")
    implementation("io.coil-kt:coil-gif:1.0.0")
    // endregion Coil

    // region Square
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.3")
    implementation("com.squareup.moshi:moshi-adapters:1.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.6.3")
    implementation("com.squareup.retrofit2:converter-moshi:2.6.3")
    // endregion Square

    // region Sugar
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    // endregion Sugar

    // region Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3-native-mt")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.12.2")
    testImplementation ("org.mockito:mockito-core:2.23.0")
    testImplementation ("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("androidx.test:core-ktx:1.3.0")
    testImplementation("androidx.test.ext:junit-ktx:1.1.2")
    // endregion Testing

}

// region Deployment

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = judoGroupId
                artifactId = judoArtifactId
                version = judoVersion

                pom {
                    name.set("Judo SDK")
                    description.set("The Judo Android SDK")
                    url.set("https://github.com/judoapp/judo-android")
                    licenses {
                        license {
                            name.set("Judo License")
                            url.set("https://github.com/judoapp/judo-android/blob/master/LICENSE")
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                url = uri(layout.projectDirectory.dir("maven"))
            }
        }
    }
}

// endregion Deployment