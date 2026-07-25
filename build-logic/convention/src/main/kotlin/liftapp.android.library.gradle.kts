plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = AndroidSdk.COMPILE

    defaultConfig {
        minSdk = AndroidSdk.MIN

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { buildConfig = true }

    // Minification is configured by `:app` only. Enabling it per module made every library run R8
    // independently, so several of them emitted a class named `a.a`, and the app's own R8 pass
    // then failed with "Type a.a is defined multiple times". A library that needs to contribute
    // keep rules should use `consumerProguardFiles`.

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

configureKotlin()

dependencies { "ksp"(libs.library("hilt-compiler")) }
