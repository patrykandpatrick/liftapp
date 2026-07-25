// `androidx.navigation.safeargs.kotlin` used to be applied here. It generated nothing: navigation
// is type-safe and serialization-based, and there is not a single navigation XML resource.
plugins {
    id("liftapp.android.library")
    id("liftapp.android.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    "implementation"(project(":core"))
    "implementation"(project(":navigation"))
    "implementation"(libs.library("kmpWheelPicker"))
}
