package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.backup.ShareBackupEvents
import com.patrykandpatrick.liftapp.core.logging.LogPublisher
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.backup.ExportRoutineUseCase
import com.patrykandpatrick.liftapp.domain.extension.moved
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.Routine
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.invoke
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.feature.routine.model.Action
import com.patrykandpatrick.liftapp.feature.routine.model.ScreenState
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.RoutineDetailsRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltViewModel
class RoutineViewModel
@Inject
constructor(
    viewModelScope: CoroutineScope,
    private val routeData: RoutineDetailsRouteData,
    private val logger: UiLogger,
    getRoutine: GetRoutineWithExercisesUseCase,
    private val getRoutineWithItems: GetRoutineWithItemsUseCase,
    private val upsertRoutine: UpsertRoutineWithItemsUseCase,
    private val deleteRoutine: DeleteRoutineUseCase,
    private val navigationCommander: NavigationCommander,
    private val exportRoutine: ExportRoutineUseCase,
    private val shareEvents: ShareBackupEvents,
    private val stringProvider: StringProvider,
) : ViewModel(viewModelScope), LogPublisher by logger {

    private val mutationMutex = Mutex()

    /** The routine the user asked to share, once it has been written to a file. */
    val share = shareEvents.events

    val screenState: StateFlow<Loadable<ScreenState>> =
        getRoutine(routeData.routineID)
            .map { routine ->
                if (routine == null) {
                    error("Routine with id ${routeData.routineID} not found, or deleted.")
                } else {
                    ScreenState(
                        name = routine.name,
                        items = routine.items,
                        primaryMuscles = routine.primaryMuscles,
                        secondaryMuscles = routine.secondaryMuscles,
                        tertiaryMuscles = routine.tertiaryMuscles,
                    )
                }
            }
            .toLoadableStateFlow(viewModelScope)

    init {
        observePickedExercises()
    }

    fun handleAction(action: Action) {
        when (action) {
            Action.Edit -> handleEdit()
            Action.Delete -> delete()
            Action.Share -> share()
            Action.PopBackStack -> popBackStack()
            Action.StartWorkout -> startWorkout()
            is Action.PickExercises -> pickExercises(action.disabledExerciseIDs)
            is Action.RemoveItem ->
                updateItems(transform = { items -> items.filterNot { it.id == action.itemID } })
            is Action.ReorderItems -> reorderItems(action)
            is Action.ReorderSupersetExercise -> reorderSupersetExercises(action)
            is Action.RemoveSupersetExercise -> removeSupersetExercise(action)
            Action.NewSuperset -> openSuperset(routineItemID = ID_NOT_SET)
            is Action.EditSuperset -> openSuperset(action.itemID)
            is Action.NavigateToExercise -> navigateToExercise(action.exerciseID)
            is Action.NavigateToExerciseGoal -> navigateToExerciseGoal(action.exerciseID)
        }
    }

    private fun handleEdit() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Routine.edit(routeData.routineID))
        }
    }

    private fun delete() {
        viewModelScope.launch {
            navigationCommander.popBackStack()
            withContext(NonCancellable) { deleteRoutine(routeData.routineID) }
        }
    }

    /** Writes the routine to a backup file of its own and hands it to whatever the user picks. */
    private fun share() {
        viewModelScope.launch {
            runCatching { exportRoutine.exportRoutine(routeData.routineID) }
                .onSuccess { file -> shareEvents.share(file) }
                .onFailure { throwable ->
                    Timber.tag(Constants.Logging.DISPLAYABLE_ERROR)
                        .e(throwable, stringProvider.errorBackupExportFailed)
                }
        }
    }

    private fun pickExercises(disabledExerciseIDs: List<Long>) {
        viewModelScope.launch {
            navigationCommander.navigateTo(
                Routes.Exercise.pick(KEY_EXERCISE_IDS, disabledExerciseIDs)
            )
        }
    }

    private fun observePickedExercises() {
        navigationCommander
            .getResults<List<Long>>(KEY_EXERCISE_IDS)
            .onEach { exerciseIDs ->
                updateItems(transform = { items -> items + exerciseIDs.map(RoutineItem::exercise) })
            }
            .launchIn(viewModelScope)
    }

    private fun openSuperset(routineItemID: Long) {
        viewModelScope.launch {
            navigationCommander.navigateTo(
                Routes.Routine.superset(routeData.routineID, routineItemID)
            )
        }
    }

    private fun reorderItems(action: Action.ReorderItems) {
        updateItems { items ->
            val itemsByID = items.associateBy(RoutineItem::id)
            action.itemIDs.mapNotNull(itemsByID::get) + items.filterNot { it.id in action.itemIDs }
        }
    }

    private fun reorderSupersetExercises(action: Action.ReorderSupersetExercise) {
        updateItems(
            transform = { items ->
                items.map { item ->
                    if (item.id == action.itemID) {
                        item.copy(
                            exerciseIDs = item.exerciseIDs.moved(action.fromIndex, action.toIndex)
                        )
                    } else {
                        item
                    }
                }
            }
        )
    }

    /**
     * Drops an exercise of a superset from the routine. A superset left with too few exercises
     * gives way to items of its own.
     */
    private fun removeSupersetExercise(action: Action.RemoveSupersetExercise) {
        updateItems(
            transform = { items ->
                items.flatMap { item ->
                    if (item.id != action.itemID) return@flatMap listOf(item)
                    val remainingExerciseIDs = item.exerciseIDs - action.exerciseID
                    if (remainingExerciseIDs.size >= RoutineItem.MIN_SUPERSET_SIZE) {
                        listOf(item.copy(exerciseIDs = remainingExerciseIDs))
                    } else {
                        remainingExerciseIDs.map(RoutineItem::exercise)
                    }
                }
            }
        )
    }

    private fun updateItems(transform: (List<RoutineItem>) -> List<RoutineItem>) {
        viewModelScope.launch {
            mutationMutex.withLock {
                val routine = getRoutineWithItems(routeData.routineID).first() ?: return@withLock
                upsertRoutine(
                    Routine(name = routine.name, id = routine.id),
                    transform(routine.items),
                )
            }
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

    private companion object {
        const val KEY_EXERCISE_IDS = "routine_exercise_ids"
    }
}
