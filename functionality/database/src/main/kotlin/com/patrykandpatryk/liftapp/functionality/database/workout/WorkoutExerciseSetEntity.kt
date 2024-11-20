package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

@Entity(
    tableName = "workout_exercise_set",
    primaryKeys = ["workout_id", "exercise_id", "exercise_set_id"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["workout_id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exercise_id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseSetEntity::class,
            parentColumns = ["exercise_set_id"],
            childColumns = ["exercise_set_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class WorkoutExerciseSetEntity(
    @ColumnInfo(name = "workout_id")
    val workoutId: Long,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long,
    @ColumnInfo(name = "exercise_set_id")
    val workoutExerciseEntryId: Long,
    @ColumnInfo(name = "workout_exercise_set_index")
    val setIndex: Int,
)
