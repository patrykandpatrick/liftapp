buildscript {
    dependencies {
        classpath(libs.hilt.gradle.plugin)
        classpath(libs.kotlin.serialization.plugin)
        classpath(libs.safeArgs)
    }

    repositories { gradlePluginPortal() }
}

plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
}
