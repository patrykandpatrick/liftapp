import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/** The SDK levels every module builds against. Previously `gradle/versions.gradle`. */
internal object AndroidSdk {
    const val MIN = 26
    const val COMPILE = 37
    const val TARGET = 37
}

/**
 * Version catalogs have no generated accessors inside convention plugins, so they are looked up by
 * name instead.
 */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow {
        IllegalArgumentException("No library '$alias' in the catalog.")
    }

internal fun VersionCatalog.bundle(alias: String): Provider<ExternalModuleDependencyBundle> =
    findBundle(alias).orElseThrow { IllegalArgumentException("No bundle '$alias' in the catalog.") }

/**
 * AGP 9 brings its own Kotlin support, so `org.jetbrains.kotlin.android` is neither applied nor
 * needed; this configures the extension AGP registers.
 */
internal fun Project.configureKotlin() {
    extensions.configure<KotlinAndroidProjectExtension> {
        jvmToolchain(21)

        compilerOptions {
            optIn.addAll(
                "kotlin.RequiresOptIn",
                "kotlin.time.ExperimentalTime",
                "kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        }
    }
}
