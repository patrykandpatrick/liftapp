package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.exercises.model.Event
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercises.model.Intent
import com.patrykandpatryk.liftapp.feature.exercises.model.ScreenState
import com.patrykandpatryk.liftapp.feature.exercises.usecase.GetExercisesItemsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

@HiltViewModel(assistedFactory = ExerciseViewModel.Factory::class)
class ExerciseViewModel
@AssistedInject
constructor(
    @Assisted val pickingMode: Boolean,
    @Assisted val disabledExerciseIDs: List<Long>?,
    getExercisesItems: GetExercisesItemsUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Event> {

    private val query = MutableStateFlow(value = "")

    private val groupBy = MutableStateFlow(value = GroupBy.Name)

    private val checkedExerciseIds: MutableSet<Long> = HashSet()

    val exercises =
        getExercisesItems(query = query, groupBy = groupBy)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    override val state: MutableStateFlow<ScreenState> =
        MutableStateFlow(ScreenState(pickingMode = pickingMode))

    override val events: MutableSharedFlow<Event> = MutableSharedFlow()

    init {
        getExercisesItems(query = query, groupBy = groupBy)
            .flowOn(dispatcher)
            .onEach { exercises ->
                state.update { state -> state.copy(exercises = exercises.updateState()) }
            }
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: Intent) =
        when (intent) {
            is Intent.SetQuery -> setQuery(intent.query)
            is Intent.SetGroupBy -> setGroupBy(intent.groupBy)
            is Intent.SetExerciseChecked -> setExerciseChecked(intent.exerciseId, intent.checked)
            is Intent.FinishPickingExercises -> finishPickingExercises()
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
            val enabled = disabledExerciseIDs?.contains(exerciseItem.id)?.not() ?: true
            exerciseItem.copy(
                checked = enabled && checkedExerciseIds.contains(exerciseItem.id),
                enabled = enabled,
            )
        } else {
            exerciseItem
        }
    }

    private fun finishPickingExercises() {
        viewModelScope.launch(dispatcher) {
            events.emit(Event.OnExercisesPicked(checkedExerciseIds.toList()))
        }
    }

    override fun close() = Unit

    @AssistedFactory
    interface Factory {
        fun create(pickingMode: Boolean, disabledExerciseIDs: List<Long>?): ExerciseViewModel
    }
}
