import com.android.build.api.dsl.CommonExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

// Applied on top of either `liftapp.android.library` or `liftapp.android.application`, so this
// deliberately does not apply an Android plugin of its own and reaches the shared DSL instead.
plugins { id("org.jetbrains.kotlin.plugin.compose") }

extensions.configure<CommonExtension> {
    buildFeatures.compose = true

    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
}

extensions.configure<KotlinAndroidProjectExtension> {
    compilerOptions {
        optIn.addAll(
            "androidx.compose.animation.ExperimentalAnimationApi",
            "androidx.compose.foundation.ExperimentalFoundationApi",
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "androidx.compose.ui.ExperimentalComposeUiApi",
            "androidx.compose.ui.text.ExperimentalTextApi",
            "androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi",
        )
    }
}

composeCompiler {
    val destination = layout.buildDirectory.dir("compose_compiler")
    reportsDestination = destination
    metricsDestination = destination
}

// `:domain` is deliberately absent. `:ui` is the design system, and keeping the domain off its
// compile classpath is what stops it from rendering models: the modules that do render them ask
// for `:domain` themselves.
dependencies {
    "implementation"(platform(libs.library("compose-bom")))
    "implementation"(libs.bundle("hilt"))
    "implementation"(libs.bundle("ui"))
    "implementation"(libs.library("activity"))
    "implementation"(libs.library("appcompat"))
    "implementation"(libs.library("lifecycle-runtime"))
    "implementation"(libs.library("timber"))
    "implementation"(libs.library("viewmodel"))

    "debugImplementation"(libs.library("compose-ui-test-manifest"))
    "debugImplementation"(libs.library("compose-ui-tooling"))

    "testImplementation"(project(":domain-testing"))
    "testImplementation"(libs.bundle("testing"))
}
