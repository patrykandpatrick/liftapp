package com.patrykandpatryk.liftapp.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.feature.bodymeasurementlist.navigation.BodyMeasurementListNavigator
import com.patrykandpatryk.liftapp.feature.dashboard.navigation.DashboardNavigator
import com.patrykandpatryk.liftapp.feature.exercises.navigation.ExerciseListNavigator
import com.patrykandpatryk.liftapp.feature.more.navigation.MoreNavigator
import com.patrykandpatryk.liftapp.feature.routines.navigation.RoutineListNavigator

@Immutable
class HomeNavigator(private val navController: NavController) : DashboardNavigator, RoutineListNavigator, ExerciseListNavigator,
    BodyMeasurementListNavigator, MoreNavigator {
    override fun onExercisesPicked(exerciseIds: List<Long>) {
        val previousRoute: String = requireNotNull(navController.previousBackStackEntry?.destination?.route)
        navController.getBackStackEntry(previousRoute).savedStateHandle[Constants.Keys.PICKED_EXERCISE_IDS] = exerciseIds
        navController.popBackStack()
    }

    override fun newExercise() {
        navController.navigate(Routes.NewExercise.create())
    }

    override fun editExercise(exerciseId: Long) {
        navController.navigate(Routes.NewExercise.create(exerciseId))
    }

    override fun back() {
        navController.popBackStack()
    }

    override fun bodyMeasurementDetails(bodyMeasurementID: Long) {
        navController.navigate(Routes.BodyMeasurementDetails.create(bodyMeasurementID))
    }

    override fun oneRepMaxCalculator() {
        navController.navigate(Routes.OneRepMax.value)
    }

    override fun settings() {
        navController.navigate(Routes.Settings.value)
    }

    override fun about() {
        navController.navigate(Routes.About.value)
    }

    override fun newRoutine() {
        navController.navigate(Routes.NewRoutine.create())
    }

    override fun editRoutine(routineId: Long) {
        navController.navigate(Routes.NewRoutine.create(routineId))
    }
}

@Composable
fun rememberHomeNavigator(navController: NavController): HomeNavigator =
    remember(navController) { HomeNavigator(navController) }
