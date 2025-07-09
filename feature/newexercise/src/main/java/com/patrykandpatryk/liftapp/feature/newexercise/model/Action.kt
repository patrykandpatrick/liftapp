package com.patrykandpatryk.liftapp.feature.newexercise.model

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

sealed interface Action {
    data class UpdateName(val name: String) : Action

    data class UpdateExerciseType(val exerciseType: ExerciseType) : Action

    data class MainMuscleListAction(val listAction: ListAction) : Action

    data class SecondaryMuscleListAction(val listAction: ListAction) : Action

    data class TertiaryMuscleListAction(val listAction: ListAction) : Action

    data object Save : Action

    data object PopBackStack : Action

    sealed interface ListAction {
        data class ToggleMuscle(val muscle: Muscle) : ListAction

        data object Clear : ListAction
    }
}
