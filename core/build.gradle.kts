apply { from("$rootDir/gradle/ui-module-base.gradle") }

plugins { alias(libs.plugins.library) }

android { namespace = "com.patrykandpatryk.liftapp.core" }

dependencies {
    api(project(":ui"))
    implementation(project(":navigation"))
    implementation(libs.opto.domain)
}
