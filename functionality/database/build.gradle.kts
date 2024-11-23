apply {
    from("$rootDir/gradle/functionality-module-base.gradle")
}

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinXSerialization)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas".toString())
}

android {
    namespace = "com.patrykandpatryk.liftapp.functionality.database"
}

dependencies {
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.opto.domain)
}
