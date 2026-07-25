// The convention plugins in `build-logic` compile against these but do not carry them, so they are
// resolved here and applied per module by the conventions themselves.
buildscript {
    dependencies {
        classpath(libs.hilt.gradle.plugin)
        classpath(libs.kotlin.serialization.plugin)
    }

    repositories { gradlePluginPortal() }
}

plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
}
