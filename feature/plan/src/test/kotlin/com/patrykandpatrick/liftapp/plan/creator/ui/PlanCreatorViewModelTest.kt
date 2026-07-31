package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.liftapp.core.preview.PreviewRoutineWithExercises
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.plan.DeletePlanContract
import com.patrykandpatrick.liftapp.domain.plan.DeletePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.plan.UpsertPlanContract
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatrick.liftapp.navigation.data.PlanCreatorRouteData
import com.patrykandpatrick.liftapp.plan.creator.model.Action
import com.patrykandpatrick.liftapp.plan.creator.model.UpsertPlanUseCase
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

class PlanCreatorViewModelTest {

    private val savedStateHandle = SavedStateHandle()

    private val formatter = Formatter(TestStringProvider, MutableStateFlow(true))

    private val textFieldStateManager =
        TextFieldStateManager(TestStringProvider, formatter, savedStateHandle)

    private val preferences = TestPreferenceRepository()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun getSut(planID: Long, plan: Plan? = null): PlanCreatorViewModel =
        PlanCreatorViewModel(
            routeData = PlanCreatorRouteData(planID),
            getPlanUseCase = GetPlanUseCase { flowOf(checkNotNull(plan)) },
            upsertPlanUseCase =
                UpsertPlanUseCase(
                    object : UpsertPlanContract {
                        override suspend fun upsertPlan(plan: Plan) = Unit
                    }
                ),
            deletePlanUseCase =
                DeletePlanUseCase(
                    deletePlanContract = DeletePlanContract {},
                    activePlan = preferences.activePlan,
                ),
            getRoutineWithExercisesUseCase = GetRoutineWithExercisesUseCase { flowOf(null) },
            textFieldStateManager = textFieldStateManager,
            savedStateHandle = savedStateHandle,
            navigationCommander = NavigationCommander(),
        )

    private suspend fun PlanCreatorViewModel.items(): List<ScreenState.Item> =
        assertIs<Loadable.Success<ScreenState>>(state.first { it is Loadable.Success }).data.items

    @Test
    fun `A new plan starts with nothing but the row that adds a day`() = runTest {
        val items = getSut(planID = ID_NOT_SET).items()

        assertEquals(listOf(ScreenState.Item.PlaceholderItem), items)
    }

    @Test
    fun `An existing plan keeps the row that adds a day`() = runTest {
        val items = getSut(planID = EXISTING_PLAN_ID, plan = existingPlan).items()

        assertEquals(existingPlan.items.size + 1, items.size)
        assertEquals(
            ScreenState.Item.PlaceholderItem,
            items.last(),
            "The row that adds a day has to close the list, because both add actions insert " +
                "directly ahead of it.",
        )
    }

    @Test
    fun `An existing plan lists its days in order, ahead of the row that adds one`() = runTest {
        val items = getSut(planID = EXISTING_PLAN_ID, plan = existingPlan).items()

        assertIs<ScreenState.Item.RoutineItem>(items[0])
        assertIs<ScreenState.Item.RestItem>(items[1])
        assertIs<ScreenState.Item.RoutineItem>(items[2])
    }

    @Test
    fun `A rest day added to an existing plan lands after its last day`() = runTest {
        val viewModel = getSut(planID = EXISTING_PLAN_ID, plan = existingPlan)
        viewModel.items()

        viewModel.onAction(Action.AddRestDay)

        val items = viewModel.items()
        assertEquals(existingPlan.items.size + 2, items.size)
        assertIs<ScreenState.Item.RestItem>(items[items.lastIndex - 1])
        assertTrue(items.last() is ScreenState.Item.PlaceholderItem)
    }

    @Test
    fun `A plan may hold the same routine on more than one day`() = runTest {
        val repeated = PreviewRoutineWithExercises.routines[0]
        val plan =
            Plan(
                id = EXISTING_PLAN_ID,
                name = "Plan",
                description = "",
                items = listOf(Plan.Item.Routine(repeated), Plan.Item.Routine(repeated)),
            )

        val items =
            getSut(planID = EXISTING_PLAN_ID, plan = plan)
                .items()
                .filterIsInstance<ScreenState.Item.RoutineItem>()

        assertEquals(2, items.size)
        assertEquals(
            items.size,
            items.map { it.id }.toSet().size,
            "Two days holding one routine need keys of their own, or the list they are drawn in " +
                "throws on the duplicate.",
        )
    }

    private companion object {
        const val EXISTING_PLAN_ID = 1L

        val existingPlan =
            Plan(
                id = EXISTING_PLAN_ID,
                name = "Plan",
                description = "",
                items =
                    listOf(
                        Plan.Item.Routine(PreviewRoutineWithExercises.routines[0]),
                        Plan.Item.Rest,
                        Plan.Item.Routine(PreviewRoutineWithExercises.routines[1]),
                    ),
            )
    }
}
