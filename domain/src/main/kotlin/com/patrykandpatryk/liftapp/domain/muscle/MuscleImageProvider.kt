package com.patrykandpatryk.liftapp.domain.muscle

interface MuscleImageProvider {

    suspend fun getMuscleImagePath(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isDark: Boolean,
    ): String

    suspend fun getMuscleImagePath(muscleContainer: MuscleContainer, isDark: Boolean): String =
        getMuscleImagePath(
            primaryMuscles = muscleContainer.primaryMuscles,
            secondaryMuscles = muscleContainer.secondaryMuscles,
            tertiaryMuscles = muscleContainer.tertiaryMuscles,
            isDark = isDark,
        )

    fun getMuscleImageName(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isDark: Boolean,
    ): String

    fun getMuscleImageName(muscleContainer: MuscleContainer, isDark: Boolean): String =
        getMuscleImageName(
            primaryMuscles = muscleContainer.primaryMuscles,
            secondaryMuscles = muscleContainer.secondaryMuscles,
            tertiaryMuscles = muscleContainer.tertiaryMuscles,
            isDark = isDark,
        )
}
