package com.patrykandpatrick.liftapp.feature.routine.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.core.model.MuscleModel
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleContainer
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem

@Immutable
data class ScreenState(
    val name: String,
    val exercises: List<RoutineExerciseItem>,
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
