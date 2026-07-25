plugins { id("liftapp.jvm.library") }

dependencies {
    implementation(project(":domain"))
    implementation(libs.bundles.testing)
}
