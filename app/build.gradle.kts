plugins {
    id("liftapp.android.application")
    id("liftapp.android.compose")
    alias(libs.plugins.oss.licenses)
}

val releaseVersionCode =
    providers.gradleProperty("releaseVersionCode").map(String::toInt).getOrElse(1)
val releaseVersionName = providers.gradleProperty("releaseVersionName").getOrElse("0.0.0")
val releaseKeystorePath = providers.environmentVariable("RELEASE_KEYSTORE_PATH").orNull

android {
    namespace = "com.patrykandpatrick.liftapp"

    defaultConfig {
        applicationId = "pl.patrykgoworowski.mintlift"
        versionCode = releaseVersionCode
        versionName = releaseVersionName
    }

    signingConfigs {
        create("release") {
            if (releaseKeystorePath != null) {
                storeFile = file(releaseKeystorePath)
                storePassword = providers.environmentVariable("RELEASE_KEYSTORE_PASSWORD").orNull
                keyAlias = providers.environmentVariable("RELEASE_KEY_ALIAS").orNull
                keyPassword = providers.environmentVariable("RELEASE_KEY_PASSWORD").orNull
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
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
