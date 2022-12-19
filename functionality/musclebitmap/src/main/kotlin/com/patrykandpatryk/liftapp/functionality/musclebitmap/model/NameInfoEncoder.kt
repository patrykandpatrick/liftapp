package com.patrykandpatryk.liftapp.functionality.musclebitmap.model

import com.patrykandpatryk.liftapp.domain.muscle.Muscle

interface NameInfoEncoder {

    fun encodeToName(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isLight: Boolean,
    ): String
}
