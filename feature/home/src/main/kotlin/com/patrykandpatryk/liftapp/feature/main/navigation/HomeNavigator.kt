package com.patrykandpatryk.liftapp.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.feature.bodymeasurementlist.navigation.BodyMeasurementListNavigator
import com.patrykandpatryk.liftapp.feature.dashboard.navigation.DashboardNavigator
import com.patrykandpatryk.liftapp.feature.exercises.navigation.ExerciseListNavigator
import com.patrykandpatryk.liftapp.feature.more.navigation.MoreNavigator
import com.patrykandpatryk.liftapp.feature.routines.navigation.RoutineListNavigator

@Immutable
class HomeNavigator(private val navController: NavController) : DashboardNavigator, RoutineListNavigator, ExerciseListNavigator,
    BodyMeasurementListNavigator, MoreNavigator {
    override fun onExercisesPicked(exerciseIDs: List<Long>) {
        val previousRoute: String = requireNotNull(navController.previousBackStackEntry?.destination?.route)
        navController.getBackStackEntry(previousRoute).savedStateHandle[Constants.Keys.PICKED_EXERCISE_IDS] = exerciseIDs
        navController.popBackStack()
    }

    override fun newExercise() {
        navController.navigate(Routes.Exercise.new())
    }

    override fun editExercise(exerciseID: Long) {
        navController.navigate(Routes.Exercise.edit(exerciseID))
    }

    override fun back() {
        navController.popBackStack()
    }

    override fun bodyMeasurementDetails(bodyMeasurementID: Long) {
        navController.navigate(Routes.BodyMeasurement.details(bodyMeasurementID))
    }

    override fun oneRepMaxCalculator() {
        navController.navigate(Routes.OneRepMax)
    }

    override fun settings() {
        navController.navigate(Routes.Settings)
    }

    override fun about() {
        navController.navigate(Routes.About)
    }

    override fun newRoutine() {
        navController.navigate(Routes.Routine.new())
    }

    override fun routine(routineID: Long) {
        navController.navigate(Routes.Routine.details(routineID))
    }
}

@Composable
fun rememberHomeNavigator(navController: NavController): HomeNavigator =
    remember(navController) { HomeNavigator(navController) }
