package com.patrykandpatryk.liftapp.functionality.database.plan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @Query(
        "SELECT p.*, plan_item_order_index, plan_item_order_index, r.*, e.*, g.*  FROM `plan` p " +
            "LEFT JOIN plan_item pi ON p.plan_id = pi.plan_item_plan_id " +
            "LEFT JOIN routine r ON pi.plan_item_routine_id = r.routine_id " +
            "LEFT JOIN exercise_with_routine ewr on ewr.routine_id = plan_item_routine_id " +
            "LEFT JOIN exercise e ON e.exercise_id = ewr.exercise_id " +
            "LEFT JOIN goal g ON g.goal_routine_id = ewr.routine_id AND g.goal_exercise_id = ewr.exercise_id " +
            "ORDER BY p.plan_id, pi.plan_item_order_index, ewr.order_index"
    )
    fun getAllPlans(): Flow<List<PlanWithRoutine>>

    @Query(
        "SELECT p.*, plan_item_order_index, r.*, e.*, g.*  FROM `plan` p " +
            "LEFT JOIN plan_item pi ON p.plan_id = pi.plan_item_plan_id " +
            "LEFT JOIN routine r ON pi.plan_item_routine_id = r.routine_id " +
            "LEFT JOIN exercise_with_routine ewr on ewr.routine_id = plan_item_routine_id " +
            "LEFT JOIN exercise e ON e.exercise_id = ewr.exercise_id " +
            "LEFT JOIN goal g ON g.goal_routine_id = ewr.routine_id AND g.goal_exercise_id = ewr.exercise_id " +
            "WHERE p.plan_id = :id ORDER BY p.plan_id, pi.plan_item_order_index, ewr.order_index"
    )
    fun getPlan(id: Long): Flow<List<PlanWithRoutine>>

    @Upsert suspend fun upsertPlan(plan: PlanEntity): Long

    @Upsert suspend fun upsertPlanItems(planItem: List<PlanItemEntity>): List<Long>

    @Query("DELETE FROM `plan` WHERE plan_id = :id") suspend fun deletePlan(id: Long)

    @Query("DELETE FROM plan_item WHERE plan_item_plan_id = :id")
    suspend fun deletePlanItems(id: Long)

    @Insert suspend fun insertPlanItemSchedule(schedule: List<PlanItemSchedule>): List<Long>

    @Query(
        "SELECT r.*, e.*, g.*  FROM plan_item_schedule p " +
            "LEFT JOIN routine r ON p.plan_item_routine_id = r.routine_id " +
            "LEFT JOIN exercise_with_routine ewr on ewr.routine_id = plan_item_routine_id " +
            "LEFT JOIN exercise e ON e.exercise_id = ewr.exercise_id " +
            "LEFT JOIN goal g ON g.goal_routine_id = ewr.routine_id AND g.goal_exercise_id = ewr.exercise_id " +
            "WHERE p.plan_item_schedule_date = :date ORDER BY ewr.order_index"
    )
    fun getScheduledRoutine(date: LocalDate): Flow<List<ScheduledRoutine>>
}
