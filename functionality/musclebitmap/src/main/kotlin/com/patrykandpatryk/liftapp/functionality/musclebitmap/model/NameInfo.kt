package com.patrykandpatryk.liftapp.functionality.musclebitmap.model

import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageType
import kotlinx.serialization.Serializable

@Serializable
data class NameInfo(
    val mainMuscles: List<Muscle>,
    val secondaryMuscles: List<Muscle>,
    val tertiaryMuscles: List<Muscle>,
    val isLight: Boolean,
    val version: Int = 1,
    val muscleImageType: MuscleImageType = MuscleImageType.FrontAndRear,
)
