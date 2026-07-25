plugins {
    id("liftapp.jvm.library")
    alias(libs.plugins.ksp)
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    api(libs.kotlinx.serialization.json)
    api(libs.bundles.kotlin)

    implementation(libs.hilt.core)

    ksp(libs.hilt.compiler)

    testImplementation(project(":domain-testing"))
    testImplementation(libs.bundles.testing)
}
