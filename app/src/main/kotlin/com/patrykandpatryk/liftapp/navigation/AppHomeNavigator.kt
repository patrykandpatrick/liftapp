package com.patrykandpatryk.liftapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
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
}

@Composable
fun rememberHomeNavigator(navController: NavController): HomeNavigator =
    remember(navController) { AppHomeNavigator(navController) }
