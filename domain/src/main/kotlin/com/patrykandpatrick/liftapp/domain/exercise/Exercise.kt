package com.patrykandpatrick.liftapp.domain.exercise

import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleContainer

/**
 * [role], [progressionPolicy] and [loadIncrement] are what the prescription math keys off: [role]
 * indexes the rep-range and RIR defaults, [progressionPolicy] decides whether a load suggestion is
 * offered at all, and [loadIncrement] is the step any suggested load is rounded to.
 */
data class Exercise(
    val id: Long,
    val displayName: String,
    val name: Name,
    val exerciseType: ExerciseType,
    override val primaryMuscles: List<Muscle>,
    override val secondaryMuscles: List<Muscle>,
    override val tertiaryMuscles: List<Muscle>,
    /** Null when the exercise has no role — `Cardio` and `Time` — or nobody has classified it. */
    val role: ExerciseRole? = null,
    val progressionPolicy: ProgressionPolicy = ProgressionPolicy.DoubleProgression,
    /** The equipment's smallest step, in kilograms. Null when the exercise carries no load. */
    val loadIncrement: Double? = null,
) : MuscleContainer {

    fun update(
        id: Long = this.id,
        name: Name = this.name,
        exerciseType: ExerciseType = this.exerciseType,
        mainMuscles: List<Muscle> = this.primaryMuscles,
        secondaryMuscles: List<Muscle> = this.secondaryMuscles,
        tertiaryMuscles: List<Muscle> = this.tertiaryMuscles,
        role: ExerciseRole? = this.role,
        progressionPolicy: ProgressionPolicy = this.progressionPolicy,
    ): Update =
        Update(
            id = id,
            name = name,
            exerciseType = exerciseType,
            mainMuscles = mainMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
            role = role,
            progressionPolicy = progressionPolicy,
        )

    data class Insert(
        val name: Name,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle> = emptyList(),
        val tertiaryMuscles: List<Muscle> = emptyList(),
        val role: ExerciseRole? = null,
        val progressionPolicy: ProgressionPolicy = ProgressionPolicy.DoubleProgression,
        val loadIncrement: Double? = null,
    )

    /**
     * Only the fields the exercise editor can change. [loadIncrement] is deliberately absent — it
     * is not editable yet, and leaving it out of the update keeps the seeded value rather than
     * writing a null over it.
     */
    data class Update(
        val id: Long,
        val name: Name,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
        val role: ExerciseRole?,
        val progressionPolicy: ProgressionPolicy,
    )
}
