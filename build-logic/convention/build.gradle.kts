plugins { `kotlin-dsl` }

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
}
