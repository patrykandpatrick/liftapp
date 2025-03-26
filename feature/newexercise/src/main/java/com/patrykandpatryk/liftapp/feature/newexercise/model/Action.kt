package com.patrykandpatryk.liftapp.feature.newexercise.model

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

sealed interface Action {
    data class UpdateName(val name: String) : Action

    data class UpdateExerciseType(val exerciseType: ExerciseType) : Action

    data class ToggleMainMuscle(val muscle: Muscle) : Action

    data class ToggleSecondaryMuscle(val muscle: Muscle) : Action

    data class ToggleTertiaryMuscle(val muscle: Muscle) : Action

    data object Save : Action

    data object PopBackStack : Action
}
