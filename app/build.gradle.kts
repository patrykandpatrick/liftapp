plugins {
    id("liftapp.android.application")
    id("liftapp.android.compose")
}

android {
    namespace = "pl.patrykgoworowski.mintlift"

    defaultConfig {
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
    implementation(project(":core"))
    implementation(project(":navigation"))
    implementation(project(":feature:about"))
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
