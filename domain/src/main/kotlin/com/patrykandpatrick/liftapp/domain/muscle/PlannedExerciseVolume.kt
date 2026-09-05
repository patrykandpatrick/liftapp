package com.patrykandpatrick.liftapp.domain.muscle

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType

/** One prescribed exercise, reduced to what the per-muscle fold needs of it. */
data class PlannedExerciseVolume(
    val setCount: Int,
    val exerciseType: ExerciseType,
    override val primaryMuscles: List<Muscle>,
    override val secondaryMuscles: List<Muscle>,
    override val tertiaryMuscles: List<Muscle>,
) : MuscleContainer
