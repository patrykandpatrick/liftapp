package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.exercises.model.Action
import com.patrykandpatryk.liftapp.feature.exercises.model.Event
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercises.model.ScreenState
import com.patrykandpatryk.liftapp.feature.exercises.usecase.GetExercisesItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ExerciseViewModel
@Inject
constructor(
    private val routeData: ExerciseListRouteData,
    getExercisesItems: GetExercisesItemsUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val navigationCommander: NavigationCommander,
) : ViewModel(), ScreenStateHandler<ScreenState, Action, Event> {

    private val query = MutableStateFlow(value = "")

    private val groupBy = MutableStateFlow(value = GroupBy.Name)

    private val checkedExerciseIds: MutableSet<Long> = HashSet()

    val exercises =
        getExercisesItems(query = query, groupBy = groupBy)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    override val state: MutableStateFlow<ScreenState> =
        MutableStateFlow(ScreenState(mode = routeData.mode))

    override val events: MutableSharedFlow<Event> = MutableSharedFlow()

    init {
        getExercisesItems(query = query, groupBy = groupBy)
            .flowOn(dispatcher)
            .onEach { exercises ->
                state.update { state -> state.copy(exercises = exercises.updateState()) }
            }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(action: Action) =
        when (action) {
            is Action.SetQuery -> setQuery(action.query)
            is Action.SetGroupBy -> setGroupBy(action.groupBy)
            is Action.SetExerciseChecked -> setExerciseChecked(action.exerciseId, action.checked)
            is Action.FinishPickingExercises -> finishPickingExercises(action.resultKey)
            is Action.GoToExerciseDetails -> goToExerciseDetails(action.exerciseID)
            Action.GoToNewExercise -> goToNewExercise()
            Action.PopBackStack -> popBackStack()
        }

    private fun setQuery(query: String) {
        state.update { state -> state.copy(query = query) }
        this.query.value = query
    }

    private fun setGroupBy(groupBy: GroupBy) {
        state.update { state -> state.copy(groupBy = groupBy) }
        this.groupBy.value = groupBy
    }

    private fun setExerciseChecked(exerciseId: Long, checked: Boolean) {
        viewModelScope.launch(dispatcher) {
            if (checked) {
                checkedExerciseIds.add(exerciseId)
            } else {
                checkedExerciseIds.remove(exerciseId)
            }
            state.update { state ->
                state.copy(
                    exercises = state.exercises.updateState(),
                    selectedItemCount = checkedExerciseIds.size,
                )
            }
        }
    }

    private fun List<ExercisesItem>.updateState(): List<ExercisesItem> = map { exerciseItem ->
        if (exerciseItem is ExercisesItem.Exercise) {
            val enabled = routeData.disabledExerciseIDs?.contains(exerciseItem.id)?.not() ?: true
            exerciseItem.copy(
                checked = enabled && checkedExerciseIds.contains(exerciseItem.id),
                enabled = enabled,
            )
        } else {
            exerciseItem
        }
    }

    private fun finishPickingExercises(resultKey: String) {
        viewModelScope.launch {
            navigationCommander.publishResult(resultKey, checkedExerciseIds.toList())
            navigationCommander.popBackStack()
        }
    }

    private fun goToExerciseDetails(exerciseID: Long) {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Exercise.details(exerciseID))
        }
    }

    private fun goToNewExercise() {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Exercise.new()) }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }
}
