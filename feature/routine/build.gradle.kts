apply { from("$rootDir/gradle/feature-module-base.gradle") }

plugins {
    alias(libs.plugins.library)
    id("org.jetbrains.kotlin.android")
}

android { namespace = "com.patrykandpatryk.liftapp.feature.routine" }
