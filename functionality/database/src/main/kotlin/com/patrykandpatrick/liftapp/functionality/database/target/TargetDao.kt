package com.patrykandpatrick.liftapp.functionality.database.target

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetDao {
    @Query(
        "SELECT * FROM exercise_target WHERE exercise_target_plan_id = :planID " +
            "AND exercise_target_cycle_index = :cycleIndex " +
            "AND exercise_target_item_order_index = :itemOrderIndex"
    )
    fun getTargets(
        planID: Long,
        cycleIndex: Int,
        itemOrderIndex: Int,
    ): Flow<List<ExerciseTargetEntity>>

    /** The position a date falls on, which is what turns "today" into a key into this table. */
    @Query(
        "SELECT plan_item_schedule_plan_id AS planID, plan_item_schedule_cycle_index AS cycleIndex, " +
            "plan_item_schedule_item_order_index AS itemOrderIndex FROM plan_item_schedule " +
            "WHERE plan_item_routine_id = :routineID AND plan_item_schedule_date = :date LIMIT 1"
    )
    suspend fun getPlanPosition(routineID: Long, date: LocalDate): PlanPosition?

    @Query(
        "SELECT * FROM exercise_target WHERE exercise_target_plan_id = :planID " +
            "AND exercise_target_cycle_index = :cycleIndex " +
            "AND exercise_target_item_order_index = :itemOrderIndex"
    )
    suspend fun getTargetsOnce(
        planID: Long,
        cycleIndex: Int,
        itemOrderIndex: Int,
    ): List<ExerciseTargetEntity>

    @Upsert suspend fun upsertTargets(targets: List<ExerciseTargetEntity>)
}
