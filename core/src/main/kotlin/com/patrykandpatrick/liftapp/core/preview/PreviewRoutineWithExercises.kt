package com.patrykandpatrick.liftapp.core.preview

import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises

object PreviewRoutineWithExercises {
    val routines =
        listOf(
            RoutineWithExercises(
                id = 1,
                name = "Push",
                items = PreviewExercises.exercises.asRoutineItems(),
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            ),
            RoutineWithExercises(
                id = 2,
                name = "Pull",
                items = PreviewExercises.exercises.asRoutineItems(),
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            ),
            RoutineWithExercises(
                id = 3,
                name = "Legs",
                items = PreviewExercises.exercises.asRoutineItems(),
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            ),
        )

    private fun List<RoutineExerciseItem>.asRoutineItems() = mapIndexed { index, exercise ->
        RoutineItemWithExercises(
            id = index.toLong() + 1,
            type = RoutineItemType.Exercise,
            exercises = listOf(exercise),
        )
    }
}
