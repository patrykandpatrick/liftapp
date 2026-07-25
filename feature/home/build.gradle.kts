plugins { id("liftapp.android.feature") }

android { namespace = "com.patrykandpatryk.liftapp.feature.home" }

// Every `:feature:home:*` module is re-exported, so `:app` only depends on `:feature:home`.
val homeModules = subprojects

dependencies {
    implementation(project(":navigation"))
    homeModules.forEach { homeModule -> api(homeModule) }
}
