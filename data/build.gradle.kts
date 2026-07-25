plugins { id("liftapp.android.library") }

android { namespace = "com.patrykandpatryk.liftapp.data" }

dependencies {
    implementation(project(":domain"))
    implementation(libs.bundles.hilt)
    implementation(libs.timber)

    testImplementation(project(":domain-testing"))
    testImplementation(libs.bundles.testing)
}
