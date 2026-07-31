package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.Constants.Workout.EXERCISE_CHANGE_DELAY
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.workout.GetActiveWorkoutsUseCase
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditWorkoutItemsUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.RESOLVED_WORKOUT_ID
import com.patrykandpatrick.liftapp.feature.workout.model.UpdateExerciseNotesUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpdateWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutIterator
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutPage
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.WorkoutRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class WorkoutViewModel
@Inject
constructor(
    getEditableWorkoutUseCase: GetEditableWorkoutUseCase,
    private val upsertGoalSets: UpsertGoalSetsUseCase,
    private val upsertExerciseSet: UpsertExerciseSetUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val updateExerciseNotesUseCase: UpdateExerciseNotesUseCase,
    private val editWorkoutItems: EditWorkoutItemsUseCase,
    private val navigationCommander: NavigationCommander,
    getActiveWorkoutsUseCase: GetActiveWorkoutsUseCase,
    workoutRouteData: WorkoutRouteData,
    coroutineScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(coroutineScope) {

    private val customPage = MutableSharedFlow<Int>()

    private val _entryState =
        MutableStateFlow<WorkoutEntryState>(
            if (workoutRouteData.workoutID != ID_NOT_SET) {
                WorkoutEntryState.Ready
            } else {
                WorkoutEntryState.Loading
            }
        )
    val entryState: StateFlow<WorkoutEntryState> = _entryState

    private val selectedItem =
        savedStateHandle.getMutableStateFlow<IntArray?>(
            "selectedExerciseAndSetIndex",
            null,
        )

    val workout: StateFlow<EditableWorkout?> =
        getEditableWorkoutUseCase()
            .transformLatest { workout ->
                emit(workout)
                if (workout.endDate == null) {
                    keepEndDateTimeUpdated(workout.summary.endDate, workout.summary.endTime)
                }
            }
            .combine(selectedItem) { workout, selectedExerciseAndSetIndex ->
                workout.copy(
                    selectedExerciseAndSet =
                        selectedExerciseAndSetIndex?.let { (exerciseIndex, setIndex) ->
                            workout.iterator.items.firstOrNull {
                                it.exerciseIndex == exerciseIndex && it.setIndex == setIndex
                            }
                        }
                )
            }
            .stateIn(coroutineScope, SharingStarted.Lazily, null)

    val selectedPage: StateFlow<Int> =
        merge(
                customPage,
                workout
                    .filterNotNull()
                    .distinctUntilChangedBy { it.startPageIndex }
                    .withIndex()
                    .transform { (index, workout) ->
                        if (index > 0) delay(EXERCISE_CHANGE_DELAY)
                        emit(workout.startPageIndex)
                    },
            )
            .stateIn(coroutineScope, SharingStarted.Lazily, 0)

    init {
        if (workoutRouteData.workoutID == ID_NOT_SET) {
            viewModelScope.launch {
                _entryState.value =
                    getActiveWorkoutsUseCase().first().firstOrNull()?.let {
                        WorkoutEntryState.ConfirmContinue(it)
                    } ?: WorkoutEntryState.Ready
            }
        }

        navigationCommander
            .getResults<List<Long>>(KEY_EXERCISE_IDS)
            .onEach { exerciseIDs -> editWorkoutItems.addExercises(getWorkout().id, exerciseIDs) }
            .launchIn(viewModelScope)
    }

    fun continueActiveWorkout() {
        val activeWorkout = (_entryState.value as? WorkoutEntryState.ConfirmContinue)?.workout
        if (activeWorkout != null) savedStateHandle[RESOLVED_WORKOUT_ID] = activeWorkout.id
        _entryState.value = WorkoutEntryState.Ready
    }

    fun cancelStartingWorkout() {
        popBackStack()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.MovePageBy -> onPageDelta(action.delta)
            is Action.SelectPage -> selectPage(action.pageIndex)
            is Action.SaveSet -> saveSet(action.workout, action.item)
            is Action.FinishWorkout -> finishWorkout()
            is Action.UpdateWorkoutName -> updateWorkoutName(action.name)
            is Action.UpdateWorkoutStartDateTime ->
                updateWorkoutStartDateTime(action.date, action.time)
            is Action.UpdateWorkoutEndDateTime -> updateWorkoutEndDateTime(action.date, action.time)
            is Action.UpdateWorkoutNotes -> updateWorkoutNotes(action.notes)
            is Action.UpdateExerciseNotes -> updateExerciseNotes(action.exercise, action.notes)
            is Action.AddSet -> updateSetCount(exercises = action.exercises, delta = 1)
            is Action.RemoveSet -> updateSetCount(exercises = action.exercises, delta = -1)
            is Action.PickExercises -> pickExercises(action.disabledExerciseIDs)
            is Action.RemoveItem -> removeItem(action.workoutItemID)
            is Action.ReorderItems -> reorderItems(action)
            is Action.GoToExerciseDetails -> goToExerciseDetails(action.exerciseID)
            is Action.PopBackStack -> popBackStack()
        }
    }

    private suspend fun keepEndDateTimeUpdated(
        endDate: TextFieldState<LocalDate>,
        endTime: TextFieldState<LocalTime>,
    ) {
        while (currentCoroutineContext().isActive) {
            endDate.updateValue(LocalDate.now())
            endTime.updateValue(LocalTime.now())
            delay((60 - LocalTime.now().second).seconds)
        }
    }

    private fun onPageDelta(delta: Int) {
        viewModelScope.launch {
            val pages = workout.filterNotNull().first().pages
            val currentPage = selectedPage.value
            val nextPage = (currentPage + delta).coerceIn(0, pages.lastIndex)
            customPage.emit(nextPage)
        }
    }

    fun selectPage(page: Int) {
        viewModelScope.launch { customPage.emit(page) }
    }

    private fun updateSetCount(exercises: List<EditableWorkout.Exercise>, delta: Int) {
        viewModelScope.launch {
            val workoutID = getWorkout().id
            val setCount = (exercises.first().sets.size + delta).coerceAtLeast(1)
            if (exercises.first().isSuperset) {
                editWorkoutItems.updateSetCount(
                    workoutID = workoutID,
                    workoutItemID = exercises.first().workoutItemID,
                    setCount = setCount,
                )
            } else {
                exercises.forEach { exercise ->
                    upsertGoalSets(workoutID, exercise, setCount)
                }
            }
        }
    }

    private suspend fun getWorkout(): EditableWorkout = workout.filterNotNull().first()

    private fun updateWorkoutName(name: TextFieldState<String>) {
        if (name.hasError) return
        viewModelScope.launch {
            val workoutID = getWorkout().id
            updateWorkoutUseCase(workoutID = workoutID, name = name.value)
        }
    }

    private fun updateWorkoutStartDateTime(
        date: TextFieldState<LocalDate>,
        time: TextFieldState<LocalTime>,
    ) {
        if (date.hasError) return
        viewModelScope.launch {
            updateWorkoutUseCase(
                workoutID = getWorkout().id,
                startDate = date.value.atTime(time.value),
            )
        }
    }

    private fun updateWorkoutEndDateTime(
        date: TextFieldState<LocalDate>,
        time: TextFieldState<LocalTime>,
    ) {
        if (date.hasError) return
        viewModelScope.launch {
            updateWorkoutUseCase(
                workoutID = getWorkout().id,
                endDate = date.value.atTime(time.value),
            )
        }
    }

    private fun updateWorkoutNotes(notes: TextFieldState<String>) {
        if (notes.hasError) return
        viewModelScope.launch {
            updateWorkoutUseCase(workoutID = getWorkout().id, notes = notes.value)
        }
    }

    private fun updateExerciseNotes(exercise: EditableWorkout.Exercise, notes: String) {
        viewModelScope.launch { updateExerciseNotesUseCase(exercise, notes) }
    }

    private fun finishWorkout() {
        viewModelScope.launch {
            returnToHome()
            withContext(NonCancellable) {
                val workout = getWorkout()
                if (workout.endDate == null) {
                    updateWorkoutUseCase(workoutID = workout.id, endDate = LocalDateTime.now())
                }
            }
        }
    }

    fun saveSet(workout: EditableWorkout, item: WorkoutIterator.Item) {
        viewModelScope.launch {
            upsertExerciseSet(workout.id, item.exercise.id, item.set, item.setIndex)
            selectedItem.value =
                workout.iterator.getNextIncomplete(item)?.let {
                    intArrayOf(it.exerciseIndex, it.setIndex)
                }
        }
    }

    private fun goToExerciseDetails(exerciseID: Long) {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Exercise.details(exerciseID))
        }
    }

    private fun pickExercises(disabledExerciseIDs: List<Long>) {
        viewModelScope.launch {
            navigationCommander.navigateTo(
                Routes.Exercise.pick(KEY_EXERCISE_IDS, disabledExerciseIDs)
            )
        }
    }

    private fun reorderItems(action: Action.ReorderItems) {
        viewModelScope.launch {
            editWorkoutItems.reorderItems(
                workoutID = getWorkout().id,
                workoutItemIDs = action.workoutItemIDs,
            )
            if (action.selectedWorkoutItemID != null) {
                val selectedPage = action.workoutItemIDs.indexOf(action.selectedWorkoutItemID)
                if (selectedPage >= 0) customPage.emit(selectedPage)
            }
        }
    }

    private fun removeItem(workoutItemID: Long) {
        viewModelScope.launch {
            val currentWorkout = getWorkout()
            val currentPage = selectedPage.value
            val selectedWorkoutItemID =
                (currentWorkout.pages.getOrNull(currentPage) as? WorkoutPage.Exercise)?.item?.id
            val removedItemStart = currentWorkout.items.indexOfFirst { it.id == workoutItemID }

            editWorkoutItems.removeItem(currentWorkout.id, workoutItemID)
            selectedItem.value = null

            val updatedWorkout =
                workout.filterNotNull().first { workout ->
                    workout.items.none { it.id == workoutItemID }
                }
            val updatedPage =
                when {
                    selectedWorkoutItemID == null -> updatedWorkout.items.size
                    selectedWorkoutItemID == workoutItemID ->
                        removedItemStart.coerceIn(0, updatedWorkout.items.size)
                    else ->
                        updatedWorkout.items.indexOfFirst { item ->
                            item.id == selectedWorkoutItemID
                        }
                }
            customPage.emit(updatedPage.coerceIn(0, updatedWorkout.items.size))
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private suspend fun returnToHome() {
        navigationCommander.navigateTo(
            NavigationCommand.Route(route = Routes.Home, popUpTo = Routes.Home)
        )
    }

    private companion object {
        const val KEY_EXERCISE_IDS = "workout_exercise_ids"
    }
}

sealed interface WorkoutEntryState {
    data object Loading : WorkoutEntryState

    data object Ready : WorkoutEntryState

    data class ConfirmContinue(val workout: Workout) : WorkoutEntryState
}
