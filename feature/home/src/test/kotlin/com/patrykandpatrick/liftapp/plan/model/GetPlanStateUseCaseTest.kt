package com.patrykandpatrick.liftapp.plan.model

import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetAllPlansUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.plan.ui.PlanState
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetPlanStateUseCaseTest {

    @Test
    fun `No active plan records whether plans can be chosen`() = runTest {
        assertEquals(
            PlanState.NoActivePlan(hasPlans = false),
            getPlanState(plans = emptyList()),
        )
        assertEquals(
            PlanState.NoActivePlan(hasPlans = true),
            getPlanState(plans = listOf(plan)),
        )
    }

    @Test
    fun `Current plan item includes its completed workout`() = runTest {
        val routine =
            RoutineWithExercises(
                id = 2L,
                name = "Push",
                items = emptyList(),
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            )
        val plan =
            Plan(
                id = 1L,
                name = "Plan",
                description = "",
                items = listOf(Plan.Item.Routine(routine)),
            )
        val workout =
            Workout(
                id = 3L,
                routineID = routine.id,
                name = routine.name,
                startDate = LocalDateTime.now().minusHours(1),
                endDate = LocalDateTime.now(),
                notes = "",
                exercises = emptyList(),
            )

        val state =
            getPlanState(
                plans = listOf(plan),
                activePlan = ActivePlan(plan.id, LocalDate.now(), cycleCount = 1),
                workouts = listOf(workout),
                selectedPlan = plan,
            )
                as PlanState.ActivePlan

        assertEquals(workout, state.currentWorkout)
    }

    private suspend fun getPlanState(
        plans: List<Plan>,
        activePlan: ActivePlan? = null,
        workouts: List<Workout> = emptyList(),
        selectedPlan: Plan = plan,
    ): PlanState {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(activePlan)
        val getActivePlan =
            GetActivePlanUseCase(
                getPlanUseCase = GetPlanUseCase { flowOf(selectedPlan) },
                activePlan = preferences.activePlan,
            )
        return GetPlanStateUseCase(
                getActivePlanUseCase = getActivePlan,
                getAllPlansUseCase = GetAllPlansUseCase { flowOf(plans) },
                getWorkoutsByDateContract = GetWorkoutsByDateContract { flowOf(workouts) },
            )
            .invoke()
            .first()
    }

    private companion object {
        val plan = Plan(id = 1L, name = "Plan", description = "", items = emptyList())
    }
}
