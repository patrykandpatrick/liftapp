package com.patrykandpatrick.liftapp.domain.routine

import com.patrykandpatrick.liftapp.domain.muscle.Muscle

data class RoutineWithExercises(
    val id: Long,
    val name: String,
    val items: List<RoutineItemWithExercises>,
    val primaryMuscles: List<Muscle>,
    val secondaryMuscles: List<Muscle>,
    val tertiaryMuscles: List<Muscle>,
) {
    val exercises: List<RoutineExerciseItem>
        get() = items.flatMap(RoutineItemWithExercises::exercises)
}
