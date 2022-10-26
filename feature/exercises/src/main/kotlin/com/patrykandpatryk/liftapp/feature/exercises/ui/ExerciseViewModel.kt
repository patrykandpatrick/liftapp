package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.exercises.di.PickingMode
import com.patrykandpatryk.liftapp.feature.exercises.model.Event
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercises.model.Intent
import com.patrykandpatryk.liftapp.feature.exercises.model.ScreenState
import com.patrykandpatryk.liftapp.feature.exercises.usecase.GetExercisesItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    @PickingMode val pickingMode: Boolean,
    getExercisesItems: GetExercisesItemsUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Event> {

    private val query = MutableStateFlow(value = "")

    private val groupBy = MutableStateFlow(value = GroupBy.Name)

    private val checkedExerciseIds: MutableSet<Long> = HashSet()

    val exercises = getExercisesItems(query = query, groupBy = groupBy)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    override val state: MutableStateFlow<ScreenState> =
        MutableStateFlow(ScreenState(pickingMode = pickingMode))

    override val events: MutableSharedFlow<Event> = MutableSharedFlow()

    init {
        getExercisesItems(query = query, groupBy = groupBy)
            .flowOn(dispatcher)
            .onEach { exercises ->
                state.update { state ->
                    state.copy(exercises = exercises.updateCheckedState())
                }
            }.launchIn(viewModelScope)
    }

    override fun handleIntent(intent: Intent) = when (intent) {
        is Intent.SetQuery -> setQuery(intent.query)
        is Intent.SetGroupBy -> setGroupBy(intent.groupBy)
        is Intent.SetExerciseChecked -> setExerciseChecked(intent.exerciseId, intent.checked)
    }

    private fun setQuery(query: String) {
        state.update { state ->
            state.copy(query = query)
        }
        this.query.value = query
    }

    private fun setGroupBy(groupBy: GroupBy) {
        state.update { state ->
            state.copy(groupBy = groupBy)
        }
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
                state.copy(exercises = state.exercises.updateCheckedState())
            }
        }
    }

    private fun List<ExercisesItem>.updateCheckedState(): List<ExercisesItem> =
        map { exerciseItem ->
            if (exerciseItem is ExercisesItem.Exercise) {
                exerciseItem.copy(
                    checked = checkedExerciseIds.contains(exerciseItem.id),
                )
            } else {
                exerciseItem
            }
        }

    override fun close() = Unit
}
