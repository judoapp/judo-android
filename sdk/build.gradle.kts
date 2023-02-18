@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("maven-publish")
}

val judoVersion: String by rootProject.extra
val judoGroupId = "app.judo"
val judoArtifactId = "judo-sdk"
val judoApiVersion = "2"
val coilVersion = "2.1.0"

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("int", "API_VERSION", judoApiVersion)
        buildConfigField("String", "LIBRARY_VERSION", "\"$judoVersion\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.1"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    namespace = "app.judo.sdk"
}

dependencies {
    // region Jetbrains
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // endregion Jetbrains

    // region AndroidX
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-process:2.5.1")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    // endregion AndroidX

    // region Compose
    implementation("androidx.compose.ui:ui:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.2.1")
    implementation("androidx.compose.material:material:1.2.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.navigation:navigation-compose:2.5.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.1")
    // endregion Compose

    // region Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    // endregion Test

    // region Square
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    implementation("com.squareup.moshi:moshi-adapters:1.13.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.6.3")
    // endregion Square

    // region Sugar
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.0")
    // endregion Sugar

    // Coil
    implementation("io.coil-kt:coil-compose:$coilVersion")
    implementation("io.coil-kt:coil-gif:$coilVersion")

    // Exoplayer
    implementation("com.google.android.exoplayer:exoplayer-core:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.18.1")
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
                url = uri(
                    System.getenv("JUDO_M2_REPO")
                        ?: layout.projectDirectory.dir("maven")
                )
            }
        }
    }
}

// endregion Deployment