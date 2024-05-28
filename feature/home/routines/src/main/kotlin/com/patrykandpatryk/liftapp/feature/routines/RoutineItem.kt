package com.patrykandpatryk.liftapp.feature.routines

import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class RoutineItem(
    val id: Long,
    val name: String,
    val exercises: ImmutableList<String>,
) {
    companion object {
        fun create(routines: List<RoutineWithExerciseNames>): ImmutableList<RoutineItem> =
            routines.map { routine ->
                RoutineItem(routine.id, routine.name, routine.exercises.toImmutableList())
            }.toImmutableList()
    }
}
