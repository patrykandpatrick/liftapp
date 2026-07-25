plugins {
    id("liftapp.android.functionality")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android { namespace = "com.patrykandpatryk.liftapp.functionality.database" }

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

dependencies {
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
}
