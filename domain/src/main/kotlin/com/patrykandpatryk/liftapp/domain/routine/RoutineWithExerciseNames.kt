package com.patrykandpatryk.liftapp.domain.routine

data class RoutineWithExerciseNames(
    val id: Long,
    val name: String,
    val exercises: List<String>,
)
