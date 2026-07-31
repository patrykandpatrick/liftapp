plugins { id("liftapp.android.feature") }

android { namespace = "com.patrykandpatrick.liftapp.feature.home" }

dependencies { implementation(libs.paging.compose) }
