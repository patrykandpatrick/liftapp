package com.patrykandpatrick.liftapp.functionality.database.target

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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

    /**
     * The day's prescription with enough of each exercise to name it, ordered as the routine lays
     * them out rather than by whatever the target rows happen to return.
     */
    @Query(
        "SELECT t.*, e.exercise_name, e.exercise_type, r.routine_name FROM exercise_target t " +
            "INNER JOIN exercise e ON e.exercise_id = t.exercise_target_exercise_id " +
            "LEFT JOIN plan_item pi ON pi.plan_item_plan_id = t.exercise_target_plan_id " +
            "AND pi.plan_item_order_index = t.exercise_target_item_order_index " +
            "LEFT JOIN routine r ON r.routine_id = pi.plan_item_routine_id " +
            "LEFT JOIN routine_item ri ON ri.routine_item_routine_id = r.routine_id " +
            "LEFT JOIN exercise_with_routine_item ewr ON ewr.routine_item_id = ri.routine_item_id " +
            "AND ewr.exercise_id = e.exercise_id " +
            "WHERE t.exercise_target_plan_id = :planID " +
            "AND t.exercise_target_cycle_index = :cycleIndex " +
            "AND t.exercise_target_item_order_index = :itemOrderIndex " +
            "ORDER BY ri.routine_item_order_index, ewr.routine_item_exercise_order_index"
    )
    fun getTargetsWithExercises(
        planID: Long,
        cycleIndex: Int,
        itemOrderIndex: Int,
    ): Flow<List<TargetWithExerciseDto>>

    @Upsert suspend fun upsertTargets(targets: List<ExerciseTargetEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMissingTargets(targets: List<ExerciseTargetEntity>)
}
