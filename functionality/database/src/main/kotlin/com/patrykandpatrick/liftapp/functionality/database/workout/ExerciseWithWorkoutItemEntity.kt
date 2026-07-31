package com.patrykandpatrick.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity

@Entity(
    tableName = "exercise_with_workout_item",
    primaryKeys = ["workout_item_id", "exercise_id"],
    foreignKeys =
        [
            ForeignKey(
                entity = WorkoutItemEntity::class,
                parentColumns = ["workout_item_id"],
                childColumns = ["workout_item_id"],
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
data class ExerciseWithWorkoutItemEntity(
    @ColumnInfo(name = "workout_item_id") val workoutItemID: Long,
    @ColumnInfo(name = "exercise_id") val exerciseID: Long,
    @ColumnInfo(name = "workout_item_exercise_order_index") val orderIndex: Int,
    @ColumnInfo(name = "workout_item_exercise_notes") val notes: String = "",
)
