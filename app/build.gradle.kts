apply { from("$rootDir/gradle/ui-module-base.gradle") }

plugins { alias(libs.plugins.application) }

android {
    namespace = "pl.patrykgoworowski.mintlift"

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        debug {
            applicationIdSuffix = ".dev"
            isDebuggable = true
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":navigation"))
    implementation(project(":feature:about"))
    implementation(project(":feature:bodymeasurementdetails"))
    implementation(project(":feature:exercisedetails"))
    implementation(project(":feature:exercisegoal"))
    implementation(project(":feature:home"))
    implementation(project(":feature:home:exercises")) // TODO avoid
    implementation(project(":feature:newexercise"))
    implementation(project(":feature:newbodymeasuremententry"))
    implementation(project(":feature:onerepmax"))
    implementation(project(":feature:newroutine"))
    implementation(project(":feature:plan:list"))
    implementation(project(":feature:plan:configurator"))
    implementation(project(":feature:plan:creator"))
    implementation(project(":feature:routine"))
    implementation(project(":feature:routineList"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:workout"))
    project(":functionality") { subprojects.forEach(::implementation) }
}
