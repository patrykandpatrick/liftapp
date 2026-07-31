plugins { id("liftapp.android.feature") }

android { namespace = "com.patrykandpatrick.liftapp.feature.bodymeasurementdetails" }

dependencies { implementation(project(":feature:newbodymeasuremententry")) }
