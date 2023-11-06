package com.patrykandpatryk.liftapp.core.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.bottomSheet
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
    const val DISABLED_EXERCISE_IDS = "disabled_exercise_ids"

    object Home : Route(value = "home") {

        val Dashboard = append(childRoute = "dashboard")
        val Routines = append(childRoute = "routines")
        val Exercises = append(childRoute = "exercises")
        val BodyMeasurementList = append(childRoute = "body_measurement_list")
        val More = append(childRoute = "more")
    }

    val About = Route(value = "about")
    val Settings = Route(value = "settings")
    val OneRepMax = Route(value = "one_rep_max")

    object Routine : Route(value = "routine/{$ARG_ID}") {

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(routineId: Long) =
            value.replace("{$ARG_ID}", routineId.toString())
    }

    object NewRoutine : Route(value = "new_routine/{$ARG_ID}") {

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(routineId: Long = 0) =
            value.replace("{$ARG_ID}", routineId.toString())
    }

    object NewBodyMeasurementEntry : Route("new_body_measurement_entry/{$ARG_ID}?$ARG_ENTRY_ID={$ARG_ENTRY_ID}") {

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
            navArgument(ARG_ENTRY_ID) {
                type = NavType.LongType
                defaultValue = ID_NOT_SET
            },
        )

        fun create(
            bodyMeasurementID: Long,
            entryId: Long = ID_NOT_SET,
        ) = value
            .replace("{$ARG_ID}", bodyMeasurementID.toString())
            .replace("{$ARG_ENTRY_ID}", entryId.toString())
    }

    object BodyMeasurementDetails : Route(value = "body_measurement_details/{$ARG_ID}") {

        override val navArguments = listOf(
            navArgument(ARG_ID) { type = NavType.LongType },
        )

        fun create(bodyMeasurementID: Long) = value.replace("{$ARG_ID}", bodyMeasurementID.toString())
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

    object Exercises : Route(
        value = "exercises" +
            "?$ARG_PICKING_MODE={$ARG_PICKING_MODE}" +
            "?$DISABLED_EXERCISE_IDS={$DISABLED_EXERCISE_IDS}",
    ) {

        override val navArguments: List<NamedNavArgument> = listOf(
            navArgument(ARG_PICKING_MODE) { type = NavType.BoolType },
            navArgument(DISABLED_EXERCISE_IDS) { type = IdsType },
        )

        fun create(pickExercises: Boolean, disabledExerciseIds: List<Long> = emptyList()) =
            value
                .replace("{$ARG_PICKING_MODE}", pickExercises.toString())
                .replace("{$DISABLED_EXERCISE_IDS}", Json.encodeToString(disabledExerciseIds))
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
