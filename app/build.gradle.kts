plugins {
    id("liftapp.android.application")
    id("liftapp.android.compose")
    alias(libs.plugins.oss.licenses)
}

android {
    namespace = "com.patrykandpatrick.liftapp"

    defaultConfig {
        applicationId = "pl.patrykgoworowski.mintlift"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        debug {
            applicationIdSuffix = ".dev"
            isDebuggable = true
        }
    }
}

val functionalityModulePaths = project(":functionality").subprojects.map { it.path }

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":navigation"))
    implementation(project(":feature:backup"))
    implementation(project(":feature:bodymeasurementdetails"))
    implementation(project(":feature:exercisedetails"))
    implementation(project(":feature:exercisegoal"))
    implementation(project(":feature:home"))
    implementation(project(":feature:newexercise"))
    implementation(project(":feature:newbodymeasuremententry"))
    implementation(project(":feature:onerepmax"))
    implementation(project(":feature:newroutine"))
    implementation(project(":feature:plan"))
    implementation(project(":feature:routine"))
    implementation(project(":feature:routineList"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:workout"))
    functionalityModulePaths.forEach { implementation(project(it)) }
}
