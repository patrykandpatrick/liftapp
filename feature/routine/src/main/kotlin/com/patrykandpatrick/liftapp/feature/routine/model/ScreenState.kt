package com.patrykandpatrick.liftapp.feature.routine.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.core.model.MuscleModel
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleContainer
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises

@Immutable
data class ScreenState(
    val name: String,
    val items: List<RoutineItemWithExercises>,
    override val primaryMuscles: List<Muscle>,
    override val secondaryMuscles: List<Muscle>,
    override val tertiaryMuscles: List<Muscle>,
) : MuscleContainer {
    val exercises: List<RoutineExerciseItem> = items.flatMap { it.exercises }

    val exerciseIDs: List<Long> = exercises.map(RoutineExerciseItem::id)

    val muscles: List<MuscleModel> =
        MuscleModel.create(
            primaryMuscles = primaryMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
        )
}
