pluginManagement.repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
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
    ":core",
    ":data",
    ":domain",
    ":domain-testing",
    ":feature:about",
    ":feature:bodymeasurementdetails",
    ":feature:exercisedetails",
    ":feature:exercisegoal",
    ":feature:home",
    ":feature:home:bodymeasurementlist",
    ":feature:home:dashboard",
    ":feature:home:exercises",
    ":feature:home:more",
    ":feature:home:plan",
    ":feature:newbodymeasuremententry",
    ":feature:newexercise",
    ":feature:newroutine",
    ":feature:onerepmax",
    ":feature:plan:list",
    ":feature:plan:configurator",
    ":feature:plan:creator",
    ":feature:routine",
    ":feature:routineList",
    ":feature:settings",
    ":feature:workout",
    ":functionality:database",
    ":functionality:musclebitmap",
    ":functionality:preference",
    ":functionality:storage",
    ":navigation",
)
