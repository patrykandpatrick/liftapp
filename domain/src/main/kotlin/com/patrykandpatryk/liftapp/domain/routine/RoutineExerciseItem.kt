package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import java.io.Serializable

data class RoutineExerciseItem(
    val id: Long,
    val name: String,
    val muscles: String,
    val type: ExerciseType,
    val goal: Goal,
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
