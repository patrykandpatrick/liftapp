apply {
    from("$rootDir/gradle/feature-module-base.gradle")
}

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.patrykandpatryk.liftapp.feature.settings"
}
