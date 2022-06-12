package com.patrykandpatryk.liftapp.domain.exercise

import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

data class Exercise(
    val id: Long,
    val name: Name,
    val exerciseType: ExerciseType,
    val mainMuscles: List<Muscle>,
    val secondaryMuscles: List<Muscle>,
    val tertiaryMuscles: List<Muscle>,
) {

    fun update(
        id: Long = this.id,
        name: Name = this.name,
        mainMuscles: List<Muscle> = this.mainMuscles,
        secondaryMuscles: List<Muscle> = this.secondaryMuscles,
        tertiaryMuscles: List<Muscle> = this.tertiaryMuscles,
    ): Update = Update(
        id = id,
        name = name,
        mainMuscles = mainMuscles,
        secondaryMuscles = secondaryMuscles,
        tertiaryMuscles = tertiaryMuscles,
    )

    data class Insert(
        val name: Name.Raw,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
    )

    data class Update(
        val id: Long,
        val name: Name,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
    )
}
