import com.ncorti.ktfmt.gradle.KtfmtExtension
import com.ncorti.ktfmt.gradle.tasks.KtfmtCheckTask
import com.ncorti.ktfmt.gradle.tasks.KtfmtFormatTask
import dev.detekt.gradle.extensions.DetektExtension
import org.gradle.api.Project

// The convention plugins in `build-logic` compile against these but do not carry them, so they are
// resolved here and applied per module by the conventions themselves.
buildscript {
    dependencies {
        classpath(libs.hilt.gradle.plugin)
        classpath(libs.kotlin.serialization.plugin)
    }

    repositories { gradlePluginPortal() }
}

plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktfmt)
}

dependencies { detektPlugins(libs.detekt.rules.ktlint.wrapper) }

detekt {
    allRules = true
    config.setFrom(rootProject.file("detekt.yml"))
    parallel = true
}

ktfmt { kotlinLangStyle() }

// The Gradle plugin currently bundles ktfmt 0.62, but the project was already standardized on 0.64.
configurations.named("ktfmt") { resolutionStrategy.force("com.facebook:ktfmt:0.64") }

val detektCheck =
    tasks.register("detektCheck") {
        group = "verification"
        description = "Runs type-aware Detekt analysis for production and test sources."
    }

fun Project.registerAndroidKtfmtTasks() {
    val androidSources = fileTree("src") { include("**/*.kt") }
    val checkAndroid =
        tasks.register<KtfmtCheckTask>("ktfmtCheckAndroid") {
            group = "verification"
            description = "Checks Android Kotlin sources with ktfmt."
            source(androidSources)
        }
    val formatAndroid =
        tasks.register<KtfmtFormatTask>("ktfmtFormatAndroid") {
            group = "formatting"
            description = "Formats Android Kotlin sources with ktfmt."
            source(androidSources)
        }

    tasks.named("ktfmtCheck") { dependsOn(checkAndroid) }
    tasks.named("ktfmtFormat") { dependsOn(formatAndroid) }
}

subprojects {
    apply(plugin = "dev.detekt")
    apply(plugin = "com.ncorti.ktfmt.gradle")

    dependencies.add("detektPlugins", rootProject.libs.detekt.rules.ktlint.wrapper)

    extensions.configure<DetektExtension> {
        allRules = true
        config.setFrom(rootProject.file("detekt.yml"))
        parallel = true
    }

    extensions.configure<KtfmtExtension> { kotlinLangStyle() }
    // Keep the formatter version identical in every project.
    configurations.named("ktfmt") { resolutionStrategy.force("com.facebook:ktfmt:0.64") }

    pluginManager.withPlugin("com.android.application") {
        registerAndroidKtfmtTasks()
        detektCheck.configure {
            dependsOn(tasks.named("detektDebug"), tasks.named("detektDebugUnitTest"))
        }
        tasks
            .matching { it.name == "detektDebugAndroidTest" }
            .configureEach { detektCheck.configure { dependsOn(this@configureEach) } }
    }
    pluginManager.withPlugin("com.android.library") {
        registerAndroidKtfmtTasks()
        detektCheck.configure {
            dependsOn(tasks.named("detektDebug"), tasks.named("detektDebugUnitTest"))
        }
        tasks
            .matching { it.name == "detektDebugAndroidTest" }
            .configureEach { detektCheck.configure { dependsOn(this@configureEach) } }
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        detektCheck.configure { dependsOn(tasks.named("detektMain"), tasks.named("detektTest")) }
    }
}
