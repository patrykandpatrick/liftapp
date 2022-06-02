package com.patrykandpatryk.liftapp.core.navigation

open class Route(val value: String) {

    fun createDestination(vararg arguments: Pair<String, Any>): String =
        arguments.fold(initial = value) { destination, argument ->
            destination.replace("{${argument.first}}", argument.second.toString())
        }

    fun append(childRoute: String): Route = Route("$value/$childRoute")

    operator fun plus(childRoute: String): Route = append(childRoute)
}

object Routes {

    const val ARG_ID = "id"

    object Home : Route(value = "home") {

        val Dashboard = append(childRoute = "dashboard")
        val More = append(childRoute = "more")
    }

    val About = Route(value = "about")
    val Settings = Route(value = "settings")
    val OneRepMax = Route(value = "one_rep_max")
}
