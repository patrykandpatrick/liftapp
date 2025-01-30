package com.patrykandpatryk.liftapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutRouteData
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.feature.main.navigation.HomeNavigator

@Immutable
class AppHomeNavigator(private val navController: NavController) : HomeNavigator {
    override fun onExercisesPicked(exerciseIDs: List<Long>) {
        val previousRoute: String =
            requireNotNull(navController.previousBackStackEntry?.destination?.route)
        navController
            .getBackStackEntry(previousRoute)
            .savedStateHandle[Constants.Keys.PICKED_EXERCISE_IDS] = exerciseIDs
        navController.popBackStack()
    }

    override fun newExercise() {
        navController.navigate(Routes.Exercise.new())
    }

    override fun exerciseDetails(exerciseID: Long) {
        navController.navigate(Routes.Exercise.details(exerciseID))
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

    override fun newWorkout(routineID: Long) {
        navController.navigate(WorkoutRouteData.new(routineID))
    }

    override fun openWorkout(workoutID: Long) {
        navController.navigate(WorkoutRouteData.edit(workoutID))
    }
}

@Composable
fun rememberHomeNavigator(navController: NavController): HomeNavigator =
    remember(navController) { AppHomeNavigator(navController) }
