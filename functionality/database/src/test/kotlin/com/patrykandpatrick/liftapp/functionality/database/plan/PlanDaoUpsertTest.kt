package com.patrykandpatrick.liftapp.functionality.database.plan

import java.time.LocalDate
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * `upsertPlanWithItems` is a `@Transaction` method with a body, so its logic can be exercised
 * against a stand-in for the statements Room generates.
 */
class PlanDaoUpsertTest {

    /**
     * Records what the transaction asks of the generated statements. `upsertPlan` answers the way
     * Room's `@Upsert` does: the new row ID when it inserts, and -1 when it updates instead.
     */
    private class RecordingPlanDao(private val insertedID: Long) : PlanDao {
        val calls = mutableListOf<String>()
        var writtenItems: List<PlanItemEntity> = emptyList()

        override suspend fun upsertPlan(plan: PlanEntity): Long {
            calls += "upsertPlan"
            return insertedID
        }

        override suspend fun upsertPlanItems(planItem: List<PlanItemEntity>): List<Long> {
            calls += "upsertPlanItems"
            writtenItems = planItem
            return planItem.map { 0L }
        }

        override suspend fun deletePlanItems(id: Long) {
            calls += "deletePlanItems"
        }

        override suspend fun deletePlan(id: Long) = Unit

        override fun getAllPlans(): Flow<List<PlanWithRoutine>> = emptyFlow()

        override fun getPlan(id: Long): Flow<List<PlanWithRoutine>> = emptyFlow()

        override suspend fun insertPlanItemSchedule(schedule: List<PlanItemSchedule>): List<Long> =
            emptyList()

        override fun getScheduledRoutine(date: LocalDate): Flow<List<ScheduledRoutine>> =
            emptyFlow()
    }

    @Test
    fun `A plan being inserted gives its days the ID it was assigned`() = runTest {
        val dao = RecordingPlanDao(insertedID = NEW_PLAN_ID)

        val planID = dao.upsertPlanWithItems(planEntity(ID_NOT_SET), listOf(planItem(ID_NOT_SET)))

        assertEquals(NEW_PLAN_ID, planID)
        assertEquals(listOf(NEW_PLAN_ID), dao.writtenItems.map { it.planId })
    }

    @Test
    fun `A plan being updated keeps its own ID rather than the -1 upsert reports`() = runTest {
        val dao = RecordingPlanDao(insertedID = UPDATED)

        val planID =
            dao.upsertPlanWithItems(
                planEntity(EXISTING_PLAN_ID),
                listOf(planItem(EXISTING_PLAN_ID)),
            )

        assertEquals(EXISTING_PLAN_ID, planID)
        assertEquals(
            listOf(EXISTING_PLAN_ID),
            dao.writtenItems.map { it.planId },
            "Writing days against -1 would breach the foreign key `plan_item` has on `plan_id`.",
        )
    }

    @Test
    fun `The days a plan had are cleared before the ones it has now are written`() = runTest {
        val dao = RecordingPlanDao(insertedID = UPDATED)

        dao.upsertPlanWithItems(planEntity(EXISTING_PLAN_ID), listOf(planItem(EXISTING_PLAN_ID)))

        assertEquals(
            listOf("upsertPlan", "deletePlanItems", "upsertPlanItems"),
            dao.calls,
            "Merging instead of replacing would leave a removed routine behind.",
        )
    }

    private companion object {
        const val ID_NOT_SET = 0L
        const val NEW_PLAN_ID = 7L
        const val EXISTING_PLAN_ID = 3L

        /** What Room's `@Upsert` returns when it updated a row instead of inserting one. */
        const val UPDATED = -1L

        fun planEntity(id: Long) =
            PlanEntity(id = id, name = "Plan", description = "", itemCount = 1)

        fun planItem(planId: Long) = PlanItemEntity(planId = planId, routineId = 1L, orderIndex = 0)
    }
}
