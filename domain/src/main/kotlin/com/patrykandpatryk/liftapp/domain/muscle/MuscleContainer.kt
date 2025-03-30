package com.patrykandpatryk.liftapp.domain.muscle

interface MuscleContainer {
    val primaryMuscles: List<Muscle>
    val secondaryMuscles: List<Muscle>
    val tertiaryMuscles: List<Muscle>
}
