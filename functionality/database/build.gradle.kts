plugins {
    id("liftapp.android.functionality")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.patrykandpatrick.liftapp.functionality.database"

    // The migration tests run the real schema under Robolectric, and MigrationTestHelper reads
    // the exported schema files — including the published app's 11.json — as test assets.
    testOptions.unitTests.isIncludeAndroidResources = true
    sourceSets { getByName("test") { assets.directories.add("$projectDir/schemas") } }
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }

dependencies {
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    testImplementation(project(":domain-testing"))
    testImplementation(libs.robolectric)
    testImplementation(libs.room.testing)
    testImplementation(libs.test.core)
    testRuntimeOnly(libs.junit.vintage.engine)
}
