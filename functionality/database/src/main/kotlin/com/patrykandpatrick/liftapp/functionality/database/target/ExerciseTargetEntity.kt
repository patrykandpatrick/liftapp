package com.patrykandpatrick.liftapp.functionality.database.target

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanEntity

/**
 * One exercise's prescription for one day of one cycle, which is where all prescription now lives.
 *
 * Identity is four real columns so the row is queryable and foreign-keyed; the per-set list is one
 * JSON payload because it is only ever read and written whole. Nothing asks for "set 3 across every
 * exercise", and the price — set fields cannot be aggregated in SQL — is a fold over a few hundred
 * rows in Kotlin.
 */
@Entity(
    tableName = "exercise_target",
    primaryKeys =
        [
            "exercise_target_plan_id",
            "exercise_target_cycle_index",
            "exercise_target_item_order_index",
            "exercise_target_exercise_id",
        ],
    indices = [Index("exercise_target_plan_id"), Index("exercise_target_exercise_id")],
    foreignKeys =
        [
            ForeignKey(
                entity = PlanEntity::class,
                parentColumns = ["plan_id"],
                childColumns = ["exercise_target_plan_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = ExerciseEntity::class,
                parentColumns = ["exercise_id"],
                childColumns = ["exercise_target_exercise_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
data class ExerciseTargetEntity(
    @ColumnInfo(name = "exercise_target_plan_id") val planID: Long,
    @ColumnInfo(name = "exercise_target_cycle_index") val cycleIndex: Int,
    @ColumnInfo(name = "exercise_target_item_order_index") val itemOrderIndex: Int,
    @ColumnInfo(name = "exercise_target_exercise_id") val exerciseID: Long,
    @ColumnInfo(name = "exercise_target_sets") val sets: List<SetTarget>,
    @ColumnInfo(name = "exercise_target_rest_time") val restTimeMillis: Long,
    @ColumnInfo(name = "exercise_target_distance_unit") val distanceUnit: LongDistanceUnit,
)
