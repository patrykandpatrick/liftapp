package com.patrykandpatryk.liftapp.feature.routines.model

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import kotlinx.collections.immutable.toImmutableList

@Immutable
data class RoutineItem(val id: Long, val name: String, val exercises: List<String>) {
    companion object {
        fun create(routines: List<RoutineWithExerciseNames>): List<RoutineItem> =
            routines.map { routine ->
                RoutineItem(routine.id, routine.name, routine.exercises.toImmutableList())
            }
    }
}
