package com.patrykandpatryk.liftapp.domain.routine

data class RoutineWithExerciseIds(val id: Long, val name: String, val exerciseIDs: List<Long>)
