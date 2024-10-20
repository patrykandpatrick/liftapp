apply {
    from("$rootDir/gradle/functionality-module-base.gradle")
}

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.patrykandpatryk.liftapp.functionality.storage"
}

dependencies {
    implementation(libs.documentfile)
}
