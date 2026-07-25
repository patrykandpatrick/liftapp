plugins { id("liftapp.android.library") }

tasks.withType(Test::class.java).configureEach { useJUnitPlatform() }

dependencies {
    "implementation"(project(":domain"))
    "implementation"(libs.bundle("hilt"))
    "implementation"(libs.bundle("kotlin"))
    "implementation"(libs.library("timber"))

    "testImplementation"(libs.bundle("testing"))

    // Running on the JUnit Platform needs the engine and the launcher on the test runtime
    // classpath. Without them Gradle 9 fails outright, and Gradle 8 silently discovered no tests.
    "testRuntimeOnly"(libs.library("jupiter-engine"))
    "testRuntimeOnly"(libs.library("junit-platform-launcher"))
}
