package com.patrykandpatrick.liftapp.domain.routine

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import java.io.Serializable

data class RoutineExerciseItem(
    val id: Long,
    val name: String,
    val muscles: String,
    val type: ExerciseType,
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
