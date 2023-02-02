package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import java.io.Serializable

data class RoutineExerciseItem(
    val id: Long,
    val name: String,
    val muscles: String,
    val type: ExerciseType,
) : Serializable {

    companion object {
        const val serialVersionUID = 1L
    }
}
