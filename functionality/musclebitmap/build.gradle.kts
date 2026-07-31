plugins {
    id("liftapp.android.functionality")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android { namespace = "com.patrykandpatrick.liftapp.functionality.musclebitmap" }

dependencies { implementation(project(":core")) }
