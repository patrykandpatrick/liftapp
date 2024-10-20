apply {
    from("$rootDir/gradle/functionality-module-base.gradle")
}

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlinXSerialization)
}

android {
    namespace = "com.patrykandpatryk.liftapp.functionality.musclebitmap"
}

dependencies {
    implementation(project(":core"))
}
