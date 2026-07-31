package com.patrykandpatrick.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity

data class WorkoutExerciseDto(
    @Embedded val item: WorkoutItemEntity,
    @Embedded val exercise: ExerciseEntity,
    @Embedded val goal: WorkoutGoalEntity?,
    @ColumnInfo(name = "workout_item_exercise_order") val exerciseOrder: Int,
    @ColumnInfo(name = "workout_item_exercise_notes") val notes: String,
    @Embedded(prefix = "current_") val currentExerciseSet: ExerciseSetEntity?,
    @Embedded(prefix = "last_") val lastExerciseSet: ExerciseSetEntity?,
)
