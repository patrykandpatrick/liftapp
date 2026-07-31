package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exercise.GetRoutineExercisesUseCase
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithItems
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.feature.routine.model.SupersetEditorState
import com.patrykandpatrick.liftapp.feature.routine.model.SupersetSavedState
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import com.patrykandpatrick.liftapp.testing.expectMostRecentSuccessData
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SupersetViewModelTest {

    private var savedItems: List<RoutineItem>? = null

    @Test
    fun `a new superset replaces the exercises it is built from`() = runTest {
        val navigationCommander = NavigationCommander()
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                routineItemID = null,
                items =
                    listOf(
                        RoutineItem.exercise(exerciseID = 1, id = 11),
                        RoutineItem.exercise(exerciseID = 2, id = 12),
                        RoutineItem.exercise(exerciseID = 3, id = 13),
                    ),
                navigationCommander = navigationCommander,
            )

        viewModel.state.test {
            assertEquals(emptyList(), expectMostRecentSuccessData().includedExercises)
            navigationCommander.pickExercises(viewModel, listOf(2L, 3L))
            viewModel.save(expectMostRecentSuccessData())
        }

        val items = checkNotNull(savedItems)
        assertEquals(
            listOf(RoutineItemType.Exercise, RoutineItemType.Superset),
            items.map(RoutineItem::type),
        )
        assertEquals(listOf(1L), items[0].exerciseIDs)
        assertEquals(listOf(2L, 3L), items[1].exerciseIDs)
    }

    @Test
    fun `a superset can be saved without any rest between its rounds`() = runTest {
        val superset = RoutineItem.superset(exerciseIDs = listOf(1, 2), id = 11)
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                routineItemID = superset.id,
                items = listOf(superset),
            )

        viewModel.state.test {
            val state = expectMostRecentSuccessData()
            state.restTime.updateValue(0)
            viewModel.save(state)
        }

        assertEquals(Duration.ZERO, checkNotNull(savedItems).single().supersetConfig?.restTime)
    }

    @Test
    fun `saving a superset with too few exercises reports an error instead of saving`() = runTest {
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                routineItemID = null,
                items = listOf(RoutineItem.exercise(exerciseID = 1, id = 11)),
            )

        viewModel.state.test {
            viewModel.save(expectMostRecentSuccessData())
            assertEquals(
                SupersetEditorState.Error.TooFewExercises,
                expectMostRecentSuccessData().error,
            )
            viewModel.clearError()
            assertEquals(null, expectMostRecentSuccessData().error)
        }

        assertEquals(null, savedItems)
    }

    @Test
    fun `the picker disables exercises already assigned to another superset`() = runTest {
        val currentSuperset = RoutineItem.superset(exerciseIDs = listOf(1, 2), id = 11)
        val otherSuperset = RoutineItem.superset(exerciseIDs = listOf(3, 4), id = 12)
        val navigationCommander = NavigationCommander()
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                routineItemID = currentSuperset.id,
                items = listOf(currentSuperset, otherSuperset),
                navigationCommander = navigationCommander,
            )

        navigationCommander.navigationCommand.test {
            viewModel.pickExercises()
            val route = (awaitItem() as NavigationCommand.Route).route as ExerciseListRouteData
            assertIs<ExerciseListRouteData.Mode.Pick>(route.mode)

            assertEquals(listOf(1L, 2L, 3L, 4L), route.disabledExerciseIDs)
        }
    }

    @Test
    fun `an exercise removed from a superset becomes an item of its own`() = runTest {
        val superset = RoutineItem.superset(exerciseIDs = listOf(1, 2, 3), id = 11)
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                routineItemID = superset.id,
                items = listOf(superset),
            )

        viewModel.state.test {
            val state = expectMostRecentSuccessData()
            assertEquals(exercises.take(3), state.includedExercises)
            viewModel.removeExercise(exercises[0])
            viewModel.save(expectMostRecentSuccessData())
        }

        val items = checkNotNull(savedItems)
        assertEquals(
            listOf(RoutineItemType.Superset, RoutineItemType.Exercise),
            items.map(RoutineItem::type),
        )
        assertEquals(listOf(2L, 3L), items[0].exerciseIDs)
        assertEquals(listOf(1L), items[1].exerciseIDs)
    }

    /** Answers the exercise picker the view model opens with [exerciseIDs]. */
    private suspend fun NavigationCommander.pickExercises(
        viewModel: SupersetViewModel,
        exerciseIDs: List<Long>,
    ) {
        navigationCommand.test {
            viewModel.pickExercises()
            val route = (awaitItem() as NavigationCommand.Route).route as ExerciseListRouteData
            publishResult((route.mode as ExerciseListRouteData.Mode.Pick).resultKey, exerciseIDs)
        }
    }

    private fun viewModel(
        scope: CoroutineScope,
        routineItemID: Long?,
        items: List<RoutineItem>,
        navigationCommander: NavigationCommander = NavigationCommander(),
    ): SupersetViewModel {
        val savedStateHandle = SavedStateHandle()
        val routine = RoutineWithItems(id = ROUTINE_ID, name = "Routine", items = items)
        val exercisesByID = exercises.associateBy(RoutineExerciseItem::id)

        return SupersetViewModel(
            viewModelScope = scope,
            routeData = Routes.Routine.superset(ROUTINE_ID, routineItemID ?: ID_NOT_SET),
            getRoutineExercises =
                GetRoutineExercisesUseCase { exerciseIDs, _ ->
                    flowOf(exerciseIDs.mapNotNull(exercisesByID::get))
                },
            getRoutineWithItems = GetRoutineWithItemsUseCase { flowOf(routine) },
            upsertRoutine =
                UpsertRoutineWithItemsUseCase { _, items ->
                    savedItems = items
                    ROUTINE_ID
                },
            savedState = SupersetSavedState(savedStateHandle),
            textFieldStateManager =
                TextFieldStateManager(
                    TestStringProvider,
                    Formatter(TestStringProvider, MutableStateFlow(true)),
                    savedStateHandle,
                ),
            navigationCommander = navigationCommander,
        )
    }

    private companion object {
        const val ROUTINE_ID = 7L
        const val ID_NOT_SET = 0L

        val exercises =
            listOf(
                RoutineExerciseItem(1L, "Squat", "Legs", ExerciseType.Weight, Goal.default),
                RoutineExerciseItem(2L, "Bench Press", "Chest", ExerciseType.Weight, Goal.default),
                RoutineExerciseItem(3L, "Deadlift", "Back", ExerciseType.Weight, Goal.default),
                RoutineExerciseItem(4L, "Row", "Back", ExerciseType.Weight, Goal.default),
            )
    }
}
