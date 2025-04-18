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
import com.patrykandpatryk.liftapp.domain.routine.DeleteExerciseFromRoutineUseCase
import com.patrykandpatryk.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatryk.liftapp.feature.routine.model.Action
import com.patrykandpatryk.liftapp.feature.routine.model.ScreenState
import com.patrykandpatryk.liftapp.feature.routine.usecase.ReorderExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class RoutineViewModel
@Inject
constructor(
    private val routeData: RoutineDetailsRouteData,
    private val logger: UiLogger,
    getRoutine: GetRoutineWithExercisesUseCase,
    private val deleteRoutine: DeleteRoutineUseCase,
    private val deleteExerciseFromRoutine: DeleteExerciseFromRoutineUseCase,
    private val reorderExercisesUseCase: ReorderExercisesUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel(), LogPublisher by logger {

    private val showDeleteDialog = MutableStateFlow(false)

    val screenState: StateFlow<Loadable<ScreenState>> =
        combine(getRoutine(routeData.routineID), showDeleteDialog) { routine, showDeleteDialog ->
                if (routine == null) {
                    error("Routine with id ${routeData.routineID} not found, or deleted.")
                } else {
                    ScreenState(
                        name = routine.name,
                        showDeleteDialog = showDeleteDialog,
                        exercises = routine.exercises,
                        primaryMuscles = routine.primaryMuscles,
                        secondaryMuscles = routine.secondaryMuscles,
                        tertiaryMuscles = routine.tertiaryMuscles,
                    )
                }
            }
            .toLoadableStateFlow(viewModelScope)

    fun handleAction(action: Action) {
        when (action) {
            Action.Edit -> handleEdit()
            Action.ShowDeleteDialog -> handleDeleteDialogVisibility(visible = true)
            Action.HideDeleteDialog -> handleDeleteDialogVisibility(visible = false)
            Action.Delete -> deleteRoutine()
            is Action.DeleteExercise -> deleteExercise(action.exerciseId)
            is Action.Reorder -> reorder(action)
            Action.PopBackStack -> popBackStack()
            Action.StartWorkout -> startWorkout()
            is Action.NavigateToExercise -> navigateToExercise(action.exerciseID)
            is Action.NavigateToExerciseGoal -> navigateToExerciseGoal(action.exerciseID)
        }
    }

    private fun reorder(reorder: Action.Reorder) {
        viewModelScope.launch {
            val exercises = ArrayList(reorder.exercises)
            val exercise = exercises.removeAt(reorder.from)
            exercises.add(reorder.to, exercise)
            reorderExercisesUseCase(routineId = routeData.routineID, exercises = exercises)
        }
    }

    private fun handleEdit() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Routine.edit(routeData.routineID))
        }
    }

    private fun deleteRoutine() {
        handleDeleteDialogVisibility(visible = false)
        viewModelScope.launch { deleteRoutine(routeData.routineID) }
    }

    private fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch {
            deleteExerciseFromRoutine(routineId = routeData.routineID, exerciseId = exerciseId)
        }
    }

    private fun handleDeleteDialogVisibility(visible: Boolean) {
        showDeleteDialog.value = visible
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
}
