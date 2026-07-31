package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class DeletePlanUseCaseTest {

    @Test
    fun `Deleting the active plan clears its selection`() = runTest {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(activePlan)
        var deletedPlanID: Long? = null
        val useCase =
            DeletePlanUseCase(
                deletePlanContract = DeletePlanContract { deletedPlanID = it },
                activePlan = preferences.activePlan,
            )

        useCase(activePlan.planID)

        assertEquals(activePlan.planID, deletedPlanID)
        assertNull(preferences.activePlan.get().first())
    }

    @Test
    fun `Deleting another plan preserves the active plan`() = runTest {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(activePlan)
        val useCase =
            DeletePlanUseCase(
                deletePlanContract = DeletePlanContract {},
                activePlan = preferences.activePlan,
            )

        useCase(activePlan.planID + 1)

        assertEquals(activePlan, preferences.activePlan.get().first())
    }

    private companion object {
        val activePlan =
            ActivePlan(planID = 1L, startDate = LocalDate.of(2026, 7, 28), cycleCount = 1)
    }
}
