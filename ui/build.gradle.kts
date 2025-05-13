apply { from("$rootDir/gradle/ui-module-base.gradle") }

plugins { alias(libs.plugins.library) }

android { namespace = "com.patrykandpatrick.liftapp.ui" }
