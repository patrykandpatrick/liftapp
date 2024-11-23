package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.Embedded
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

data class WorkoutExerciseDto(
    @Embedded
    val exercise: ExerciseEntity,
    @Embedded
    val goal: WorkoutGoalEntity?,
    @Embedded
    val set: ExerciseSetEntity?,
)
