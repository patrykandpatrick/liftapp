package com.patrykandpatryk.liftapp.feature.exercise.model

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.core.model.MuscleModel
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.muscle.MuscleContainer

@Immutable
data class ScreenState(
    val name: String,
    val showDeleteDialog: Boolean,
    override val primaryMuscles: List<Muscle>,
    override val secondaryMuscles: List<Muscle>,
    override val tertiaryMuscles: List<Muscle>,
) : MuscleContainer {
    val muscles: List<MuscleModel> =
        MuscleModel.create(
            primaryMuscles = primaryMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
        )
}
