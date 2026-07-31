package com.patrykandpatrick.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity

@Entity(
    tableName = "exercise_with_routine_item",
    primaryKeys = ["routine_item_id", "exercise_id"],
    foreignKeys =
        [
            ForeignKey(
                entity = RoutineItemEntity::class,
                parentColumns = ["routine_item_id"],
                childColumns = ["routine_item_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = ExerciseEntity::class,
                parentColumns = ["exercise_id"],
                childColumns = ["exercise_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices = [Index(value = ["exercise_id"])],
)
data class ExerciseWithRoutineItemEntity(
    @ColumnInfo(name = "routine_item_id") val routineItemID: Long,
    @ColumnInfo(name = "exercise_id") val exerciseID: Long,
    @ColumnInfo(name = "routine_item_exercise_order_index") val orderIndex: Int,
)
