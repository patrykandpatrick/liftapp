package com.patrykandpatryk.liftapp.core.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET

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
    const val ARG_ENTRY_ID = "entry_id"

    object Home : Route(value = "home") {

        val Dashboard = append(childRoute = "dashboard")
        val Routines = append(childRoute = "routines")
        val Exercises = append(childRoute = "exercises")
        val Body = append(childRoute = "body")
        val More = append(childRoute = "more")
    }

    val About = Route(value = "about")
    val Settings = Route(value = "settings")
    val OneRepMax = Route(value = "one_rep_max")

    object InsertBodyEntry : Route(value = "insert_body_entry/{$ARG_ID}?$ARG_ENTRY_ID={$ARG_ENTRY_ID}") {

        val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
            navArgument(ARG_ENTRY_ID) {
                type = NavType.LongType
                defaultValue = ID_NOT_SET
            },
        )

        fun create(
            bodyId: Long,
            entryId: Long = ID_NOT_SET,
        ) = value
            .replace("{$ARG_ID}", bodyId.toString())
            .replace("{$ARG_ENTRY_ID}", entryId.toString())
    }

    object BodyDetails : Route(value = "body/{$ARG_ID}") {

        val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(bodyId: Long) = value.replace("{$ARG_ID}", bodyId.toString())
    }

    object NewExercise : Route(value = "new_exercise?$ARG_ID{$ARG_ID}") {

        val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(exerciseId: Long = 0) = value.replace("{$ARG_ID}", exerciseId.toString())
    }

    object Exercise : Route(value = "exercise?$ARG_ID{$ARG_ID}") {

        val navArguments = listOf(navArgument(ARG_ID) { type = NavType.LongType })

        fun create(exerciseId: Long = 0) = value.replace("{$ARG_ID}", exerciseId.toString())
    }
}
