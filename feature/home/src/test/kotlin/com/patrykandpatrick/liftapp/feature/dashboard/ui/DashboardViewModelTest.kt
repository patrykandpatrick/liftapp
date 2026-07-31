package com.patrykandpatrick.liftapp.feature.dashboard.ui

import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

class DashboardViewModelTest {

    private val today = LocalDate.of(2026, 7, 29)
    private val routine = routine(id = 42)

    @Test
    fun `Workout action skips a rest day and selects the next routine`() {
        val plan =
            Plan(id = 1, name = null, description = "", items = listOf(Plan.Item.Rest, routine))

        assertEquals(
            routine.routine.id,
            DashboardViewModel.getNextRoutineID(
                activePlan = ActivePlan(plan.id, today, cycleCount = 1) to plan,
                today = today,
            ),
        )
    }

    @Test
    fun `Workout action has no target after the active schedule ends`() {
        val plan = Plan(id = 1, name = null, description = "", items = listOf(routine))

        assertNull(
            DashboardViewModel.getNextRoutineID(
                activePlan = ActivePlan(plan.id, today.minusDays(1), cycleCount = 1) to plan,
                today = today,
            )
        )
    }

    @Test
    fun `Workout action advances after the current planned workout is completed`() {
        val nextRoutine = routine(id = 43)
        val plan =
            Plan(
                id = 1,
                name = null,
                description = "",
                items = listOf(routine, nextRoutine),
            )

        assertEquals(
            nextRoutine.routine.id,
            DashboardViewModel.getNextRoutineID(
                activePlan = ActivePlan(plan.id, today, cycleCount = 1) to plan,
                today = today,
                skipCurrentPlanItem = true,
            ),
        )
    }

    @Test
    fun `Workout action has no target after the final planned workout is completed`() {
        val plan = Plan(id = 1, name = null, description = "", items = listOf(routine))

        assertNull(
            DashboardViewModel.getNextRoutineID(
                activePlan = ActivePlan(plan.id, today, cycleCount = 1) to plan,
                today = today,
                skipCurrentPlanItem = true,
            )
        )
    }

    private fun routine(id: Long) =
        Plan.Item.Routine(
            RoutineWithExercises(
                id = id,
                name = "Routine",
                items = emptyList(),
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            )
        )
}
