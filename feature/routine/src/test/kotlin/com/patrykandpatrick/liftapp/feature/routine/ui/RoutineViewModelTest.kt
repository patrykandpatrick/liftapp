package com.patrykandpatrick.liftapp.feature.routine.ui

import app.cash.turbine.test
import com.patrykandpatrick.liftapp.core.backup.ShareBackupEvents
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.exception.DisplayableException
import com.patrykandpatrick.liftapp.domain.mapper.Mapper
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithItems
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.feature.routine.model.Action
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.SupersetDetailsRouteData
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RoutineViewModelTest {

    @Test
    fun `creating a superset opens the superset screen without a routine item`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val navigationCommander = NavigationCommander()
        val viewModel =
            viewModel(
                scope = CoroutineScope(dispatcher),
                items = listOf(RoutineItem.exercise(exerciseID = 1, id = 11)),
                navigationCommander = navigationCommander,
            )

        navigationCommander.navigationCommand.test {
            viewModel.handleAction(Action.NewSuperset)
            val route = (awaitItem() as NavigationCommand.Route).route as SupersetDetailsRouteData
            assertEquals(ROUTINE_ID, route.routineID)
            assertEquals(ID_NOT_SET, route.routineItemID)
        }
    }

    @Test
    fun `editing a superset opens its dedicated screen`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val navigationCommander = NavigationCommander()
        val superset = RoutineItem.superset(exerciseIDs = listOf(1, 2, 3), id = 11)
        val viewModel =
            viewModel(
                scope = CoroutineScope(dispatcher),
                items = listOf(superset),
                navigationCommander = navigationCommander,
            )

        navigationCommander.navigationCommand.test {
            viewModel.handleAction(Action.EditSuperset(superset.id))
            val route = (awaitItem() as NavigationCommand.Route).route as SupersetDetailsRouteData
            assertEquals(ROUTINE_ID, route.routineID)
            assertEquals(superset.id, route.routineItemID)
        }
    }

    @Test
    fun `removing an exercise of a superset drops it from the routine`() = runTest {
        var savedItems: List<RoutineItem>? = null
        val superset = RoutineItem.superset(exerciseIDs = listOf(1, 2, 3), id = 11)
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                items = listOf(superset, RoutineItem.exercise(exerciseID = 4, id = 12)),
                onSave = { savedItems = it },
            )

        viewModel.handleAction(Action.RemoveSupersetExercise(superset.id, exerciseID = 2))

        val items = checkNotNull(savedItems)
        assertEquals(
            listOf(RoutineItemType.Superset, RoutineItemType.Exercise),
            items.map(RoutineItem::type),
        )
        assertEquals(listOf(1L, 3L), items[0].exerciseIDs)
        assertEquals(listOf(4L), items[1].exerciseIDs)
    }

    @Test
    fun `a superset left with too few exercises gives way to items of their own`() = runTest {
        var savedItems: List<RoutineItem>? = null
        val superset = RoutineItem.superset(exerciseIDs = listOf(1, 2), id = 11)
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                items = listOf(superset),
                onSave = { savedItems = it },
            )

        viewModel.handleAction(Action.RemoveSupersetExercise(superset.id, exerciseID = 2))

        val items = checkNotNull(savedItems)
        assertEquals(listOf(RoutineItemType.Exercise), items.map(RoutineItem::type))
        assertEquals(listOf(listOf(1L)), items.map(RoutineItem::exerciseIDs))
    }

    @Test
    fun `reordering items saves the supplied item order`() = runTest {
        var savedItems: List<RoutineItem>? = null
        val items =
            listOf(
                RoutineItem.exercise(exerciseID = 1, id = 11),
                RoutineItem.exercise(exerciseID = 2, id = 12),
                RoutineItem.exercise(exerciseID = 3, id = 13),
            )
        val viewModel =
            viewModel(
                scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
                items = items,
                onSave = { savedItems = it },
            )

        viewModel.handleAction(Action.ReorderItems(listOf(13, 11, 12)))

        assertEquals(listOf(13L, 11L, 12L), checkNotNull(savedItems).map(RoutineItem::id))
    }

    private fun viewModel(
        scope: CoroutineScope,
        items: List<RoutineItem>,
        navigationCommander: NavigationCommander = NavigationCommander(),
        onSave: (List<RoutineItem>) -> Unit = {},
    ): RoutineViewModel {
        val routine = RoutineWithItems(id = ROUTINE_ID, name = "Routine", items = items)
        val getRoutineWithItems = GetRoutineWithItemsUseCase { flowOf(routine) }
        val getRoutineWithExercises = GetRoutineWithExercisesUseCase {
            flowOf(
                RoutineWithExercises(
                    id = ROUTINE_ID,
                    name = routine.name,
                    items =
                        items.map { item ->
                            RoutineItemWithExercises(
                                id = item.id,
                                type = item.type,
                                exercises = emptyList(),
                                supersetConfig = item.supersetConfig,
                            )
                        },
                    primaryMuscles = emptyList(),
                    secondaryMuscles = emptyList(),
                    tertiaryMuscles = emptyList(),
                )
            )
        }
        val logger =
            UiLogger(
                isDebug = false,
                exceptionMapper =
                    object : Mapper<DisplayableException, String> {
                        override suspend fun map(input: DisplayableException) = input.toString()
                    },
                dispatcher = UnconfinedTestDispatcher(),
            )

        return RoutineViewModel(
            viewModelScope = scope,
            routeData = Routes.Routine.details(ROUTINE_ID),
            logger = logger,
            getRoutine = getRoutineWithExercises,
            getRoutineWithItems = getRoutineWithItems,
            upsertRoutine =
                UpsertRoutineWithItemsUseCase { _, savedItems ->
                    onSave(savedItems)
                    ROUTINE_ID
                },
            deleteRoutine = DeleteRoutineUseCase {},
            navigationCommander = navigationCommander,
            exportRoutine = { error("The routine is not shared in these tests.") },
            shareEvents = ShareBackupEvents { location -> location },
            stringProvider = TestStringProvider,
        )
    }

    private companion object {
        const val ROUTINE_ID = 7L
    }
}
