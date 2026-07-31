plugins {
    id("liftapp.android.library")
    id("liftapp.android.compose")
}

android { namespace = "com.patrykandpatrick.liftapp.core" }

dependencies {
    api(project(":ui"))
    implementation(project(":domain"))
    implementation(project(":navigation"))
    implementation(libs.kmpWheelPicker)
}
