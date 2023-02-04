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
    val prettyGoal: String,
) : Serializable {

    companion object {
        const val serialVersionUID = 1L
    }
}
