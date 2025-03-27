package com.patrykandpatryk.liftapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.feature.routine.navigator.RoutineNavigator

@Stable
class MainNavigator(private val navController: NavController) {

    fun back() {
        navController.popBackStack()
    }

    fun getRoutineNavigator(routineID: Long): RoutineNavigator =
        object : RoutineNavigator {
            override fun back() {
                navController.popBackStack()
            }

            override fun editRoutine() {
                navController.navigate(Routes.Routine.edit(routineID))
            }

            override fun exercise(exerciseID: Long) {
                navController.navigate(Routes.Exercise.details(exerciseID))
            }

            override fun exerciseGoal(exerciseID: Long) {
                navController.navigate(Routes.Exercise.goal(routineID, exerciseID))
            }

            override fun newWorkout() {
                navController.navigate(Routes.Workout.new(routineID))
            }
        }
}

@Composable
fun rememberMainNavigator(navController: NavController): MainNavigator =
    remember(navController) { MainNavigator(navController) }
