plugins {
    id("liftapp.android.functionality")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android { namespace = "com.patrykandpatrick.liftapp.functionality.backup" }

dependencies {
    implementation(project(":functionality:database"))
    implementation(project(":functionality:preference"))
    implementation(libs.datastore)
    implementation(libs.documentfile)
    implementation(libs.room.ktx)
    implementation(libs.work)
    testImplementation(project(":domain-testing"))
}
