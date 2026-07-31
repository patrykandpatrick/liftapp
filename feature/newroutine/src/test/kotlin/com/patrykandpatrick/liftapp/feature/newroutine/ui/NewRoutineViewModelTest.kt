package com.patrykandpatrick.liftapp.feature.newroutine.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.exception.RoutineNotFoundException
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.Routine
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithItems
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineUseCase
import com.patrykandpatrick.liftapp.feature.newroutine.model.Action
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.RoutineDetailsRouteData
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import com.patrykandpatrick.liftapp.testing.expectMostRecentErrorThrowable
import com.patrykandpatrick.liftapp.testing.expectMostRecentSuccessData
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val formatter = Formatter(TestStringProvider, MutableStateFlow(true))

    private val savedStateHandle = SavedStateHandle()

    private val getRoutineWithItemsUseCase = GetRoutineWithItemsUseCase { id ->
        if (id == EXISTING_ROUTINE_ID) {
            flowOf(RoutineWithItems(id, ROUTINE_NAME, emptyList()))
        } else {
            flowOf(null)
        }
    }

    private val textFieldStateManager =
        TextFieldStateManager(TestStringProvider, formatter, savedStateHandle)

    private val navigationCommander = NavigationCommander()

    private var savedRoutine: Routine? = null

    private fun getSut(routineID: Long): NewRoutineViewModel =
        NewRoutineViewModel(
            viewModelScope = coroutineScope,
            getRoutineWithItemsUseCase = getRoutineWithItemsUseCase,
            textFieldStateManager = textFieldStateManager,
            routeData = Routes.Routine.edit(routineID),
            upsertRoutine =
                UpsertRoutineUseCase { routine ->
                    savedRoutine = routine
                    routine.id.takeIf { it != ID_NOT_SET } ?: CREATED_ROUTINE_ID
                },
            navigationCommander = navigationCommander,
            savedStateHandle = savedStateHandle,
        )

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
    fun `Given routineID is set, the routine name is loaded`() = runTest {
        getSut(routineID = EXISTING_ROUTINE_ID).state.test {
            val state = expectMostRecentSuccessData()
            assertTrue(state.isEdit)
            assertEquals(ROUTINE_NAME, state.name.value)
        }
    }

    @Test
    fun `Given routine without a name, when user tries to save it, then validation error is shown`() =
        runTest {
            val sut = getSut(routineID = ID_NOT_SET)
            sut.state.test {
                val state = expectMostRecentSuccessData()
                sut.onAction(Action.SaveRoutine)
                assertTrue(state.name.hasError)
                assertEquals(TestStringProvider.fieldCannotBeEmpty(), state.name.errorMessage)
                assertEquals(null, savedRoutine)
            }
        }

    @Test
    fun `Given a new routine with a name, when user saves it, it is saved and opened`() = runTest {
        turbineScope {
            val sut = getSut(routineID = ID_NOT_SET)
            val navigationCommand = navigationCommander.navigationCommand.testIn(this)
            sut.state.test {
                expectMostRecentSuccessData().name.updateText("Routine")
                sut.onAction(Action.SaveRoutine)
                assertEquals(Routine("Routine", ID_NOT_SET), savedRoutine)
                assertInstanceOf<NavigationCommand.PopBackStack>(navigationCommand.awaitItem())
                assertEquals(
                    CREATED_ROUTINE_ID,
                    assertInstanceOf<RoutineDetailsRouteData>(
                            assertInstanceOf<NavigationCommand.Route>(navigationCommand.awaitItem())
                                .route
                        )
                        .routineID,
                )
                navigationCommand.cancel()
            }
        }
    }

    private companion object {
        const val EXISTING_ROUTINE_ID = 1L
        const val NON_EXISTENT_ROUTINE_ID = 2L
        const val CREATED_ROUTINE_ID = 3L
        const val ROUTINE_NAME = "name"
    }
}
