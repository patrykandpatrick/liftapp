pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "LiftApp"

include(
    ":app",
    ":ui",
    ":core",
    ":domain",
    ":domain-testing",
    ":feature:backup",
    ":feature:bodymeasurementdetails",
    ":feature:exercisedetails",
    ":feature:exercisegoal",
    ":feature:home",
    ":feature:newbodymeasuremententry",
    ":feature:newexercise",
    ":feature:newroutine",
    ":feature:onerepmax",
    ":feature:plan",
    ":feature:routine",
    ":feature:routineList",
    ":feature:settings",
    ":feature:workout",
    ":functionality:backup",
    ":functionality:database",
    ":functionality:musclebitmap",
    ":functionality:preference",
    ":navigation",
)
