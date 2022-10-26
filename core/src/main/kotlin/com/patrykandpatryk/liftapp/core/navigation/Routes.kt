package com.patrykandpatryk.liftapp.core.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.bottomSheet
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET

open class Route(val value: String) {

    open val navArguments: List<NamedNavArgument> = emptyList()

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
    const val ARG_PICKING_MODE = "picking_mode"

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
    val NewRoutine = Route(value = "new_routine")

    object InsertBodyEntry : Route(value = "insert_body_entry/{$ARG_ID}?$ARG_ENTRY_ID={$ARG_ENTRY_ID}") {

        override val navArguments = listOf(
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

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(bodyId: Long) = value.replace("{$ARG_ID}", bodyId.toString())
    }

    // TODO convert to two destinations (new & edit)
    object NewExercise : Route(value = "new_exercise?$ARG_ID={$ARG_ID}") {

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(exerciseId: Long = 0) = value.replace("{$ARG_ID}", exerciseId.toString())
    }

    object Exercise : Route(value = "exercise?$ARG_ID{$ARG_ID}") {

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(exerciseId: Long = 0) = value.replace("{$ARG_ID}", exerciseId.toString())
    }

    object EditRoutine : Route(value = "edit_routine/{$ARG_ID}") {

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(exerciseId: Long) = value.replace("{$ARG_ID}", exerciseId.toString())
    }

    object Exercises : Route(value = "exercises?$ARG_PICKING_MODE={$ARG_PICKING_MODE}") {

        override val navArguments: List<NamedNavArgument> = listOf(
            navArgument(ARG_PICKING_MODE) { type = NavType.BoolType },
        )

        fun create(pickExercises: Boolean) =
            value.replace("{$ARG_PICKING_MODE}", pickExercises.toString())
    }
}

fun NavGraphBuilder.composable(
    route: Route,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route.value,
        arguments = route.navArguments,
        content = content,
    )
}

fun NavGraphBuilder.bottomSheet(
    route: Route,
    content: @Composable ColumnScope.(backstackEntry: NavBackStackEntry) -> Unit,
) {
    bottomSheet(
        route = route.value,
        arguments = route.navArguments,
        content = content,
    )
}
