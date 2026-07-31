package com.patrykandpatrick.liftapp.feature.dashboard.model

import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetPlanScheduleItemUseCaseTest {

    @Test
    fun `An unscheduled day records whether there is an active plan`() = runTest {
        assertEquals(
            PlanScheduleItem.None(hasActivePlan = false),
            getScheduleItem(activePlan = null),
        )
        assertEquals(
            PlanScheduleItem.None(hasActivePlan = true),
            getScheduleItem(activePlan = activePlan),
        )
    }

    private suspend fun getScheduleItem(activePlan: ActivePlan?): PlanScheduleItem {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(activePlan)
        val getActivePlan =
            GetActivePlanUseCase(
                getPlanUseCase = GetPlanUseCase { flowOf(plan) },
                activePlan = preferences.activePlan,
            )
        return GetPlanScheduleItemUseCase(
                getPlanItemContract =
                    object : GetPlanItemContract {
                        override fun getPlanItem(date: LocalDate) = flowOf<Plan.Item?>(null)
                    },
                getWorkoutsByDateContract = GetWorkoutsByDateContract { flowOf(emptyList()) },
                getActivePlanUseCase = getActivePlan,
            )
            .invoke(LocalDate.of(2026, 7, 28))
            .first()
    }

    private companion object {
        val activePlan =
            ActivePlan(planID = 1L, startDate = LocalDate.of(2026, 7, 28), cycleCount = 1)

        val plan =
            Plan(id = activePlan.planID, name = "Plan", description = "", items = emptyList())
    }
}
