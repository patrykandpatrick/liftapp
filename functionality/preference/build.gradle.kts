plugins { id("liftapp.android.functionality") }

android { namespace = "com.patrykandpatrick.liftapp.functionality.preference" }

dependencies {
    implementation(libs.datastore)
    testImplementation(project(":domain-testing"))
    testImplementation(libs.robolectric)
    testImplementation(libs.test.core)
    testRuntimeOnly(libs.junit.vintage.engine)
}
