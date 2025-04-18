package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.ExerciseDetailsRouteData
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.exercise.DeleteExerciseUseCase
import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.feature.exercise.model.Action
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class ExerciseDetailsViewModel
@Inject
constructor(
    private val routeData: ExerciseDetailsRouteData,
    private val logger: UiLogger,
    getExercise: GetExerciseUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val deleteExercise: DeleteExerciseUseCase,
    private val stringProvider: StringProvider,
    private val navigationCommander: NavigationCommander,
) : ViewModel(), LogPublisher by logger {

    val screenState: StateFlow<Loadable<ScreenState>> =
        combine(
                getExercise(routeData.exerciseID),
                savedStateHandle.getStateFlow(SHOW_DELETE_DIALOG_KEY, false),
            ) { exercise, showDeleteDialog ->
                if (exercise == null) {
                    error("Exercise with id ${routeData.exerciseID} not found, or deleted.")
                } else {
                    ScreenState(
                        name = stringProvider.getResolvedName(exercise.name),
                        showDeleteDialog = showDeleteDialog,
                        primaryMuscles = exercise.primaryMuscles,
                        secondaryMuscles = exercise.secondaryMuscles,
                        tertiaryMuscles = exercise.tertiaryMuscles,
                    )
                }
            }
            .toLoadableStateFlow(viewModelScope)

    fun handleIntent(action: Action) {
        when (action) {
            Action.Delete -> deleteExercise()
            Action.Edit -> sendEditExerciseEvent()
            Action.HideDeleteDialog -> setShowDeleteDialog(false)
            Action.ShowDeleteDialog -> setShowDeleteDialog(true)
            Action.PopBackStack -> popBackStack()
        }
    }

    private fun sendEditExerciseEvent() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Exercise.edit(routeData.exerciseID))
        }
    }

    private fun setShowDeleteDialog(show: Boolean) {
        savedStateHandle[SHOW_DELETE_DIALOG_KEY] = show
    }

    private fun deleteExercise() {
        setShowDeleteDialog(false)
        viewModelScope.launch { deleteExercise(routeData.exerciseID) }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    companion object {
        private const val SHOW_DELETE_DIALOG_KEY = "showDeleteDialog"
    }
}
