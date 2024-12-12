package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

@Entity(
    tableName = "workout_with_exercise",
    primaryKeys = ["workout_id", "exercise_id"],
    foreignKeys =
        [
            ForeignKey(
                entity = WorkoutEntity::class,
                parentColumns = ["workout_id"],
                childColumns = ["workout_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = ExerciseEntity::class,
                parentColumns = ["exercise_id"],
                childColumns = ["exercise_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
class WorkoutWithExerciseEntity(
    @ColumnInfo(name = "workout_id") val workoutId: Long,
    @ColumnInfo(name = "exercise_id", index = true) val exerciseId: Long,
    @ColumnInfo(name = "order_index") val orderIndex: Int,
)
