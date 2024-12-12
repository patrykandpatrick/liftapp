apply { from("$rootDir/gradle/functionality-module-base.gradle") }

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.ksp)
}

android { namespace = "com.patrykandpatryk.liftapp.functionality.preference" }

dependencies {
    implementation(libs.datastore)
    implementation(libs.bundles.opto)
    testImplementation(project(":domain-testing"))
}
