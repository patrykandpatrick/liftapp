package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.exercise.GetRoutineExercisesUseCase
import com.patrykandpatrick.liftapp.domain.exercise.invoke
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.Routine
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithItems
import com.patrykandpatrick.liftapp.domain.routine.SupersetConfig
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.invoke
import com.patrykandpatrick.liftapp.domain.validation.validNumber
import com.patrykandpatrick.liftapp.domain.validation.valueInRange
import com.patrykandpatrick.liftapp.feature.routine.model.SupersetEditorState
import com.patrykandpatrick.liftapp.feature.routine.model.SupersetSavedState
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.SupersetDetailsRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class SupersetViewModel
@Inject
constructor(
    viewModelScope: CoroutineScope,
    private val routeData: SupersetDetailsRouteData,
    private val getRoutineExercises: GetRoutineExercisesUseCase,
    private val getRoutineWithItems: GetRoutineWithItemsUseCase,
    private val upsertRoutine: UpsertRoutineWithItemsUseCase,
    private val savedState: SupersetSavedState,
    textFieldStateManager: TextFieldStateManager,
    private val navigationCommander: NavigationCommander,
) : ViewModel(viewModelScope) {

    private val sets =
        textFieldStateManager.intTextField(
            validators = {
                validNumber()
                valueInRange(Goal.setRange)
            }
        )

    private val restTime =
        textFieldStateManager.longTextField(
            validators = {
                validNumber()
                valueInRange(min = 0.0)
            }
        )

    private val includedExercises: Flow<List<RoutineExerciseItem>> =
        savedState.exerciseIDs.flatMapLatest { exerciseIDs ->
            if (exerciseIDs.isEmpty()) {
                flowOf(emptyList())
            } else {
                getRoutineExercises(exerciseIDs, true)
            }
        }

    private val error = MutableStateFlow<SupersetEditorState.Error?>(null)

    private val initialized = flow {
        val routine = checkNotNull(getRoutineWithItems(routeData.routineID).first())
        loadSuperset(routine.findSuperset())
        emit(Unit)
    }

    val state =
        combine(initialized, includedExercises, error) { _, includedExercises, error ->
                SupersetEditorState(
                    includedExercises = includedExercises,
                    sets = sets,
                    restTime = restTime,
                    error = error,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    init {
        navigationCommander
            .getResults<List<Long>>(KEY_EXERCISE_IDS)
            .onEach(savedState::addExerciseIDs)
            .launchIn(viewModelScope)
    }

    fun removeExercise(exercise: RoutineExerciseItem) {
        savedState.removeExerciseID(exercise.id)
    }

    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        savedState.reorderExerciseIDs(fromIndex, toIndex)
    }

    fun pickExercises() {
        viewModelScope.launch {
            val routine = getRoutineWithItems(routeData.routineID).first() ?: return@launch
            val currentExerciseIDs =
                if (savedState.isInitialized) {
                    savedState.exerciseIDs.value
                } else {
                    routine.findSuperset()?.exerciseIDs.orEmpty()
                }
            val disabledExerciseIDs = buildList {
                addAll(currentExerciseIDs)
                routine.items
                    .filter {
                        it.type == RoutineItemType.Superset && it.id != routeData.routineItemID
                    }
                    .forEach { addAll(it.exerciseIDs) }
            }
                .distinct()
            navigationCommander.navigateTo(
                Routes.Exercise.pick(KEY_EXERCISE_IDS, disabledExerciseIDs)
            )
        }
    }

    fun clearError() {
        error.value = null
    }

    fun save(state: SupersetEditorState) {
        sets.updateErrorMessages()
        restTime.updateErrorMessages()
        // The inputs show their own error messages, so only the exercise count needs reporting.
        error.value =
            when {
                state.includedExercises.size < RoutineItem.MIN_SUPERSET_SIZE ->
                    SupersetEditorState.Error.TooFewExercises
                state.includedExercises.size > RoutineItem.MAX_SUPERSET_SIZE ->
                    SupersetEditorState.Error.TooManyExercises
                else -> null
            }
        if (error.value != null || sets.hasError || restTime.hasError) return

        viewModelScope.launch {
            val routine = getRoutineWithItems(routeData.routineID).first() ?: return@launch
            val items =
                routine.items.withSuperset(
                    exerciseIDs = state.includedExercises.map(RoutineExerciseItem::id),
                    config = SupersetConfig(sets.value, restTime.value.milliseconds),
                )
            upsertRoutine(Routine(name = routine.name, id = routine.id), items)
            navigationCommander.popBackStack()
        }
    }

    fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    /** Returns the superset being edited, or `null` when a new one is being created. */
    private fun RoutineWithItems.findSuperset(): RoutineItem? {
        if (routeData.routineItemID == ID_NOT_SET) return null
        val item =
            checkNotNull(items.find { it.id == routeData.routineItemID }) {
                "Superset ${routeData.routineItemID} does not exist."
            }
        check(item.type == RoutineItemType.Superset) {
            "Routine item ${item.id} is not a superset."
        }
        return item
    }

    private fun loadSuperset(superset: RoutineItem?) {
        if (savedState.isInitialized) return
        savedState.isInitialized = true
        savedState.setExerciseIDs(superset?.exerciseIDs.orEmpty())
        val config = superset?.supersetConfig ?: SupersetConfig()
        sets.updateValue(config.sets)
        restTime.updateValue(config.restTime.inWholeMilliseconds)
    }

    /**
     * Returns a copy of this list holding the edited superset, creating it if it does not exist
     * yet. Items whose exercise joined the superset are dropped, and exercises that left it become
     * items of their own. The superset takes the place of the first item it replaces.
     */
    private fun List<RoutineItem>.withSuperset(
        exerciseIDs: List<Long>,
        config: SupersetConfig,
    ): List<RoutineItem> {
        val edited = find { it.id == routeData.routineItemID && it.id != ID_NOT_SET }
        val superset =
            edited?.copy(exerciseIDs = exerciseIDs, supersetConfig = config)
                ?: RoutineItem.superset(exerciseIDs = exerciseIDs, config = config)
        val includedExerciseIDs = exerciseIDs.toSet()
        val leftExerciseIDs = edited?.exerciseIDs.orEmpty() - includedExerciseIDs

        val items = mutableListOf<RoutineItem>()
        var isSupersetPlaced = false
        forEach { item ->
            val isReplaced =
                item === edited ||
                    item.type == RoutineItemType.Exercise &&
                        item.exerciseIDs.single() in includedExerciseIDs
            when {
                !isReplaced -> items.add(item)
                isSupersetPlaced -> Unit
                else -> {
                    isSupersetPlaced = true
                    items.add(superset)
                    items.addAll(leftExerciseIDs.map(RoutineItem::exercise))
                }
            }
        }
        if (!isSupersetPlaced) items.add(superset)
        return items
    }

    private companion object {
        const val KEY_EXERCISE_IDS = "superset_picked_exercise_ids"
    }
}
