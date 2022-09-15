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
val judoComposeVersion = "1.0.2"
val useLocalJudoCompose = false

// holdover from a previous attempt to rewrite the renderer, do not use.
val useRenderTree = false

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("int", "API_VERSION", judoApiVersion)
        buildConfigField("String", "LIBRARY_VERSION", "\"$judoVersion\"")
        buildConfigField("boolean", "USE_RENDER_TREE", "Boolean.parseBoolean(\"$useRenderTree\")")
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    namespace = "app.judo.sdk"
}

dependencies {
    // region Jetbrains
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    // endregion Jetbrains

    // region Judo Exoplayer
    implementation ("app.judo.libs:exoplayer-core:2.12.0")
    implementation ("app.judo.libs:exoplayer-hls:2.12.0")
    implementation ("app.judo.libs:exoplayer-dash:2.12.0")
    implementation ("app.judo.libs:exoplayer-ui:2.12.0")
    // endregion Judo Exoplayer

    // region AndroidX
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
    implementation("androidx.lifecycle:lifecycle-process:2.5.0")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    // endregion AndroidX

    // region Coil
    implementation("io.coil-kt:coil-base:2.1.0")
    implementation("io.coil-kt:coil-gif:2.1.0")
    // endregion Coil

    // region Square
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    implementation("com.squareup.moshi:moshi-adapters:1.13.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.6.3")
    // endregion Square

    // region Sugar
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.0")
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


    // region Compose
    implementation("androidx.compose.ui:ui:1.2.0")
    implementation("androidx.compose.foundation:foundation:1.2.0")
    implementation("androidx.compose.material:material:1.2.0")
    implementation("androidx.activity:activity-compose:1.5.1")
    if (useLocalJudoCompose) {
        // when developers are working on their local workstations, we use a sibling directory with the SDK. Use that if present.
        implementation(project(":judo-compose"))
    } else {
        // when running a build elsewhere (such as App Center/CI) build with the released version.
        implementation("app.judo:compose:$judoComposeVersion")
    }
    // endregion Compose
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