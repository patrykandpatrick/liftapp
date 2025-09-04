package com.patrykandpatryk.liftapp.domain.exerciseset

import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import java.time.LocalDateTime

data class ExerciseSetGroup(
    val workoutID: Long,
    val workoutName: String,
    val exerciseID: Long,
    val sets: List<ExerciseSet>,
    val workoutStartDate: LocalDateTime,
)
