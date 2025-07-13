package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exception.RoutineNotFoundException
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExerciseIDsUseCase
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseIds
import com.patrykandpatryk.liftapp.feature.newroutine.model.Action
import com.patrykandpatryk.liftapp.feature.newroutine.model.GetExerciseItemsUseCase
import com.patrykandpatryk.liftapp.feature.newroutine.model.NewRoutineSavedState
import com.patrykandpatryk.liftapp.testing.TestStringProvider
import com.patrykandpatryk.liftapp.testing.expectMostRecentErrorThrowable
import com.patrykandpatryk.liftapp.testing.expectMostRecentSuccessData
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.jupiter.api.assertInstanceOf

class NewRoutineViewModelTest {

    private val testScheduler = TestCoroutineScheduler()

    private val coroutineContext = UnconfinedTestDispatcher(testScheduler)

    private val coroutineScope = CoroutineScope(coroutineContext)

    private val formatter = Formatter(TestStringProvider, flowOf(true))

    private val savedStateHandle = SavedStateHandle()

    private val getRoutineWithExerciseIDsUseCase = GetRoutineWithExerciseIDsUseCase { id ->
        if (id == EXISTING_ROUTINE_ID) {
            flowOf(RoutineWithExerciseIds(id, "name", exerciseItems.map { it.id }))
        } else {
            flowOf(null)
        }
    }

    private val deleteRoutineUseCase: DeleteRoutineUseCase = DeleteRoutineUseCase { routineID -> }

    private val textFieldStateManager =
        TextFieldStateManager(TestStringProvider, formatter, savedStateHandle)

    private val newRoutineSavedState = NewRoutineSavedState(savedStateHandle)

    private val navigationCommander = NavigationCommander()

    private fun getSut(routineID: Long): NewRoutineViewModel {
        val routeData = Routes.Routine.edit(routineID, "")
        return NewRoutineViewModel(
            viewModelScope = coroutineScope,
            getRoutineWithExerciseIDsUseCase = getRoutineWithExerciseIDsUseCase,
            getExerciseItemsUseCase =
                GetExerciseItemsUseCase(newRoutineSavedState) { ids, _ ->
                    flowOf(exerciseItems.filter { ids.contains(it.id) })
                },
            textFieldStateManager = textFieldStateManager,
            routeData = routeData,
            newRoutineSavedState = newRoutineSavedState,
            listValidator = NonEmptyCollectionValidator(TestStringProvider),
            upsertRoutine = { _, _ -> 1L },
            navigationCommander = navigationCommander,
            deleteRoutineUseCase = deleteRoutineUseCase,
        )
    }

    @Test
    fun `Given routineID is not set, when the success state is loaded, it is NOT in the edit mode`() =
        runTest {
            getSut(routineID = ID_NOT_SET).state.test {
                assertFalse(expectMostRecentSuccessData().isEdit)
            }
        }

    @Test
    fun `Given non-existent routine id, when the routine is loaded, then the state is error`() =
        runTest {
            getSut(routineID = NON_EXISTENT_ROUTINE_ID).state.test {
                assertInstanceOf<RoutineNotFoundException>(expectMostRecentErrorThrowable())
            }
        }

    @Test
    fun `Given routineID is set, routine data is loaded`() = runTest {
        getSut(routineID = EXISTING_ROUTINE_ID).state.test {
            val state = expectMostRecentSuccessData()
            assertTrue(state.name.value.isNotEmpty())
            assertEquals(exerciseItems, state.exercises.value)
            assertTrue(state.exercises.isValid)
        }
    }

    @Test
    fun `Given exercise is picked, the exercise is added to the list of exercise items`() =
        runTest {
            val sut = getSut(routineID = ID_NOT_SET)
            sut.state.test {
                var state = expectMostRecentSuccessData()
                assertTrue(state.exercises.value.isEmpty())
                sut.onAction(Action.AddExercises(exerciseItems.map { it.id }))
                state = expectMostRecentSuccessData()
                assertEquals(exerciseItems, state.exercises.value)
            }
        }

    @Test
    fun `Given exercise is removed, the exercise is removed from the list of exercise items`() =
        runTest {
            val sut = getSut(routineID = EXISTING_ROUTINE_ID)
            sut.state.test {
                assertEquals(exerciseItems, expectMostRecentSuccessData().exercises.value)
                sut.onAction(Action.RemoveExercise(exerciseItems.first().id))
                assertEquals(exerciseItems.drop(1), expectMostRecentSuccessData().exercises.value)
            }
        }

    @Test
    fun `Given routine without a name, when user tries to save it, then validation error is shown`() =
        runTest {
            val sut = getSut(routineID = ID_NOT_SET)
            sut.state.test {
                val state = expectMostRecentSuccessData()
                sut.onAction(Action.SaveRoutine(state))
                assertTrue(state.name.hasError)
                assertEquals(TestStringProvider.fieldCannotBeEmpty(), state.name.errorMessage)
                assertTrue(expectMostRecentSuccessData().showErrors)
            }
        }

    @Test
    fun `Given routine without exercises, when user tries to save it, then validation error is shown`() =
        runTest {
            val sut = getSut(routineID = ID_NOT_SET)
            sut.state.test {
                val state = expectMostRecentSuccessData()
                sut.onAction(Action.SaveRoutine(state))
                assertTrue(state.exercises.isInvalid)
                assertEquals(
                    TestStringProvider.getErrorCannotBeEmpty(TestStringProvider.list),
                    state.exercises.errorMessage,
                )
                assertTrue(expectMostRecentSuccessData().showErrors)
            }
        }

    @Test
    fun `Given routine with a name and exercises is about to be saved, routine is saved`() =
        runTest {
            turbineScope {
                val sut = getSut(routineID = ID_NOT_SET)
                val navigationCommand = navigationCommander.navigationCommand.testIn(this)
                sut.state.test {
                    expectMostRecentSuccessData().name.updateText("Routine")
                    sut.onAction(Action.AddExercises(exerciseItems.map { it.id }))
                    sut.onAction(Action.SaveRoutine(expectMostRecentSuccessData()))
                    assertInstanceOf<NavigationCommand.PopBackStack>(navigationCommand.awaitItem())
                    navigationCommand.cancel()
                }
            }
        }

    companion object {
        private const val EXISTING_ROUTINE_ID = 1L
        private const val NON_EXISTENT_ROUTINE_ID = 2L

        private val exerciseItems =
            listOf(
                RoutineExerciseItem(1L, "name1", "description1", ExerciseType.Weight, Goal.default),
                RoutineExerciseItem(2L, "name2", "description2", ExerciseType.Reps, Goal.default),
            )
    }
}
