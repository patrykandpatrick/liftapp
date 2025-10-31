plugins {
    id("java-library")
    alias(libs.plugins.kotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin { jvmToolchain(21) }

dependencies {
    implementation(project(":domain"))
    implementation(libs.opto.domain)
    implementation(libs.bundles.testing)
}
