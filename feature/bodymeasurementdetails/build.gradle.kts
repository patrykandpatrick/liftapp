plugins { id("liftapp.android.feature") }

android { namespace = "com.patrykandpatryk.liftapp.feature.bodymeasurementdetails" }

dependencies { implementation(project(":feature:newbodymeasuremententry")) }
