plugins {
    id("liftapp.android.functionality")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android { namespace = "com.patrykandpatryk.liftapp.functionality.musclebitmap" }

dependencies { implementation(project(":core")) }
