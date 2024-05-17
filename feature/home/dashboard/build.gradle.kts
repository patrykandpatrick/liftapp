apply {
    from("$rootDir/gradle/feature-module-base.gradle")
}

plugins {
    alias(libs.plugins.library)
}

android {
    namespace = "com.patrykandpatryk.liftapp.feature.dashboard"
}
