package com.patrykandpatryk.liftapp.feature.routine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.RoutineDetailsRouteData
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatryk.liftapp.feature.routine.model.Action
import com.patrykandpatryk.liftapp.feature.routine.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class RoutineViewModel
@Inject
constructor(
    private val routeData: RoutineDetailsRouteData,
    private val logger: UiLogger,
    getRoutine: GetRoutineWithExercisesUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel(), LogPublisher by logger {

    val screenState: StateFlow<Loadable<ScreenState>> =
        getRoutine(routeData.routineID)
            .map { routine ->
                if (routine == null) {
                    error("Routine with id ${routeData.routineID} not found, or deleted.")
                } else {
                    ScreenState(
                        name = routine.name,
                        exercises = routine.exercises,
                        primaryMuscles = routine.primaryMuscles,
                        secondaryMuscles = routine.secondaryMuscles,
                        tertiaryMuscles = routine.tertiaryMuscles,
                    )
                }
            }
            .toLoadableStateFlow(viewModelScope)

    init {
        observeRoutineDeleteResult()
    }

    fun handleAction(action: Action) {
        when (action) {
            Action.Edit -> handleEdit()
            Action.PopBackStack -> popBackStack()
            Action.StartWorkout -> startWorkout()
            is Action.NavigateToExercise -> navigateToExercise(action.exerciseID)
            is Action.NavigateToExerciseGoal -> navigateToExerciseGoal(action.exerciseID)
        }
    }

    private fun handleEdit() {
        viewModelScope.launch {
            navigationCommander.navigateTo(
                Routes.Routine.edit(routeData.routineID, ROUTINE_DELETE_RESULT_KEY)
            )
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun startWorkout() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Workout.new(routeData.routineID))
        }
    }

    private fun navigateToExercise(exerciseID: Long) {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Exercise.details(exerciseID))
        }
    }

    private fun navigateToExerciseGoal(exerciseID: Long) {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Exercise.goal(routeData.routineID, exerciseID))
        }
    }

    private fun observeRoutineDeleteResult() {
        navigationCommander
            .getResults<Boolean>(ROUTINE_DELETE_RESULT_KEY)
            .filter { it }
            .onEach { navigationCommander.popBackStack() }
            .launchIn(viewModelScope)
    }

    private companion object {
        const val ROUTINE_DELETE_RESULT_KEY = "routine_delete_result_key"
    }
}
