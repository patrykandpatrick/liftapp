package com.patrykandpatrick.liftapp.functionality.database.workout

import androidx.room.Embedded
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity

data class WorkoutExerciseDto(
    @Embedded val exercise: ExerciseEntity,
    @Embedded val goal: WorkoutGoalEntity?,
    @Embedded(prefix = "current_") val currentExerciseSet: ExerciseSetEntity?,
    @Embedded(prefix = "last_") val lastExerciseSet: ExerciseSetEntity?,
)
