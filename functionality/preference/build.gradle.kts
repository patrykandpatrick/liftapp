plugins { id("liftapp.android.functionality") }

android { namespace = "com.patrykandpatrick.liftapp.functionality.preference" }

dependencies {
    implementation(libs.datastore)
    testImplementation(project(":domain-testing"))
}
