package com.patrykandpatrick.liftapp.core.preview

import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleVolume

/**
 * A push, pull and legs week that reaches all four bands. Reaching the top one is the point:
 * without it no preview draws the warm end of the scale, or the track past the recoverable ceiling
 * that `VolumeScale.MAXIMUM` exists to leave room for.
 */
val PreviewMuscleVolume: MuscleVolume =
    MuscleVolume.of(
        sets = 10,
        primaryMuscles = listOf(Muscle.Chest),
        secondaryMuscles = listOf(Muscle.FrontDelts, Muscle.Triceps),
        tertiaryMuscles = emptyList(),
    ) +
        MuscleVolume.of(
            sets = 8,
            primaryMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.Chest),
            tertiaryMuscles = listOf(Muscle.FrontDelts),
        ) +
        MuscleVolume.of(
            sets = 8,
            primaryMuscles = listOf(Muscle.Triceps),
            secondaryMuscles = listOf(Muscle.FrontDelts),
            tertiaryMuscles = emptyList(),
        ) +
        MuscleVolume.of(
            sets = 10,
            primaryMuscles = listOf(Muscle.Lats),
            secondaryMuscles = listOf(Muscle.Biceps),
            tertiaryMuscles = listOf(Muscle.RearDelts, Muscle.Forearms),
        ) +
        MuscleVolume.of(
            sets = 9,
            primaryMuscles = listOf(Muscle.Quadriceps),
            secondaryMuscles = listOf(Muscle.Glutes),
            tertiaryMuscles = listOf(Muscle.Hamstrings, Muscle.Calves),
        )
