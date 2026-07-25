apply { from("$rootDir/gradle/ui-module-base.gradle") }

plugins {
    alias(libs.plugins.library)
    id("kotlinx-serialization")
}

android { namespace = "com.patrykandpatrick.liftapp.navigation" }
