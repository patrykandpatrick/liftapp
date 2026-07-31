package com.patrykandpatrick.liftapp.functionality.database.plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @Query(
        "SELECT p.*, plan_item_order_index, r.*, e.*, g.*  FROM `plan` p " +
            "LEFT JOIN plan_item pi ON p.plan_id = pi.plan_item_plan_id " +
            "LEFT JOIN routine r ON pi.plan_item_routine_id = r.routine_id " +
            "LEFT JOIN routine_item ri ON ri.routine_item_routine_id = plan_item_routine_id " +
            "LEFT JOIN exercise_with_routine_item ewr ON ewr.routine_item_id = ri.routine_item_id " +
            "LEFT JOIN exercise e ON e.exercise_id = ewr.exercise_id " +
            "LEFT JOIN goal g ON g.goal_routine_id = r.routine_id AND g.goal_exercise_id = ewr.exercise_id " +
            "ORDER BY p.plan_id, pi.plan_item_order_index, ri.routine_item_order_index, ewr.routine_item_exercise_order_index"
    )
    fun getAllPlans(): Flow<List<PlanWithRoutine>>

    @Query(
        "SELECT p.*, plan_item_order_index, r.*, e.*, g.*  FROM `plan` p " +
            "LEFT JOIN plan_item pi ON p.plan_id = pi.plan_item_plan_id " +
            "LEFT JOIN routine r ON pi.plan_item_routine_id = r.routine_id " +
            "LEFT JOIN routine_item ri ON ri.routine_item_routine_id = plan_item_routine_id " +
            "LEFT JOIN exercise_with_routine_item ewr ON ewr.routine_item_id = ri.routine_item_id " +
            "LEFT JOIN exercise e ON e.exercise_id = ewr.exercise_id " +
            "LEFT JOIN goal g ON g.goal_routine_id = r.routine_id AND g.goal_exercise_id = ewr.exercise_id " +
            "WHERE p.plan_id = :id ORDER BY p.plan_id, pi.plan_item_order_index, " +
            "ri.routine_item_order_index, ewr.routine_item_exercise_order_index"
    )
    fun getPlan(id: Long): Flow<List<PlanWithRoutine>>

    @Upsert suspend fun upsertPlan(plan: PlanEntity): Long

    @Upsert suspend fun upsertPlanItems(planItem: List<PlanItemEntity>): List<Long>

    /**
     * Writes a plan together with the days it consists of, replacing whatever days it had before so
     * that removing one sticks.
     *
     * `@Upsert` answers with the new row ID when it inserts and with `-1` when it updates instead,
     * so an existing plan keeps the ID it came in with.
     */
    @Transaction
    suspend fun upsertPlanWithItems(plan: PlanEntity, items: List<PlanItemEntity>): Long {
        val planID = upsertPlan(plan).takeIf { it != NOT_INSERTED } ?: plan.id
        deletePlanItems(planID)
        upsertPlanItems(items.map { item -> item.copy(planId = planID) })
        return planID
    }

    @Query("DELETE FROM `plan` WHERE plan_id = :id") suspend fun deletePlan(id: Long)

    @Query("DELETE FROM plan_item WHERE plan_item_plan_id = :id")
    suspend fun deletePlanItems(id: Long)

    @Insert suspend fun insertPlanItemSchedule(schedule: List<PlanItemSchedule>): List<Long>

    @Query(
        "SELECT r.*, e.*, g.*  FROM plan_item_schedule p " +
            "LEFT JOIN routine r ON p.plan_item_routine_id = r.routine_id " +
            "LEFT JOIN routine_item ri ON ri.routine_item_routine_id = r.routine_id " +
            "LEFT JOIN exercise_with_routine_item ewr ON ewr.routine_item_id = ri.routine_item_id " +
            "LEFT JOIN exercise e ON e.exercise_id = ewr.exercise_id " +
            "LEFT JOIN goal g ON g.goal_routine_id = r.routine_id AND g.goal_exercise_id = ewr.exercise_id " +
            "WHERE p.plan_item_schedule_date = :date ORDER BY ri.routine_item_order_index, " +
            "ewr.routine_item_exercise_order_index"
    )
    fun getScheduledRoutine(date: LocalDate): Flow<List<ScheduledRoutine>>

    private companion object {
        /** What `@Upsert` returns when it updated an existing row rather than inserting one. */
        const val NOT_INSERTED = -1L
    }
}
