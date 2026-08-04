import dev.detekt.gradle.Detekt

plugins {
    `kotlin-dsl`
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktfmt)
}

kotlin { jvmToolchain(21) }

// These are `compileOnly` because the convention plugins only need the plugin APIs to compile
// against; the consuming build supplies them at execution time.
dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.kotlin.serialization.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    detektPlugins(libs.detekt.rules.ktlint.wrapper)
}

detekt {
    allRules = true
    config.setFrom(rootProject.file("../detekt.yml"))
    parallel = true
}

// Kotlin DSL adds generated accessors to its source sets and its precompiled scripts cannot be
// type-resolved by Detekt. Ktfmt still checks the scripts; Detekt checks regular Kotlin files.
tasks.named<Detekt>("detektMain") {
    setSource(files("src/main/kotlin"))
    include("**/*.kt")
}

tasks.named<Detekt>("detektTest") {
    setSource(files("src/test/kotlin"))
    include("**/*.kt")
}

ktfmt { kotlinLangStyle() }

// Match the formatter version used by the main build.
configurations.named("ktfmt") { resolutionStrategy.force("com.facebook:ktfmt:0.64") }
