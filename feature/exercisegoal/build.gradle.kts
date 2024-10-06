apply {
    from("$rootDir/gradle/feature-module-base.gradle")
}

plugins {
    alias(libs.plugins.library)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace =  "com.patrykandpatrick.liftapp.feature.exercisegoal"
}