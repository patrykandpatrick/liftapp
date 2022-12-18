package com.patrykandpatryk.liftapp.domain.muscle

interface MuscleImageProvider {

    suspend fun getMuscleImagePath(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
    ): String
}
