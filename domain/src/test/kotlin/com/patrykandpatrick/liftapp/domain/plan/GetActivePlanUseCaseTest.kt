package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.domain.exception.PlanNotFoundException
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetActivePlanUseCaseTest {

    @Test
    fun `A selected plan that has been deleted becomes no active plan`() = runTest {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(activePlan)
        val useCase =
            GetActivePlanUseCase(
                getPlanUseCase =
                    GetPlanUseCase {
                        flow { throw PlanNotFoundException(activePlan.planID) }
                    },
                activePlan = preferences.activePlan,
            )

        assertNull(useCase().first())
        assertNull(preferences.activePlan.get().first())
    }

    @Test
    fun `An existing selected plan is returned`() = runTest {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(activePlan)
        val useCase =
            GetActivePlanUseCase(
                getPlanUseCase = GetPlanUseCase { flowOf(plan) },
                activePlan = preferences.activePlan,
            )

        assertEquals(activePlan to plan, useCase().first())
    }

    private companion object {
        val activePlan =
            ActivePlan(planID = 1L, startDate = LocalDate.of(2026, 7, 28), cycleCount = 1)

        val plan =
            Plan(id = activePlan.planID, name = "Plan", description = "", items = emptyList())
    }
}
