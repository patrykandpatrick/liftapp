package pl.patrykgoworowski.mintlift.core.navigation

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

    val Main = Route("main")

    object Menu : Route("menu") {

        val Settings = append("settings")
        val About = append("about")

        val children = listOf(
            Settings,
            About,
        )
    }
}
