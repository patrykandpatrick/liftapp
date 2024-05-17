apply {
    from("$rootDir/gradle/feature-module-base.gradle")
}

plugins {
    alias(libs.plugins.library)
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
}

dependencies {
    project.subprojects.forEach { project -> add("implementation", project) }
}

android {
    namespace = "com.patrykandpatryk.liftapp.feature.home"
}
