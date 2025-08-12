package com.patrykandpatryk.liftapp.core.preview

import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises

object PreviewRoutineWithExercises {
    val routines =
        listOf(
            RoutineWithExercises(
                id = 1,
                name = "Push",
                exercises = PreviewExercises.exercises,
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            ),
            RoutineWithExercises(
                id = 2,
                name = "Pull",
                exercises = PreviewExercises.exercises,
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            ),
            RoutineWithExercises(
                id = 3,
                name = "Legs",
                exercises = PreviewExercises.exercises,
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            ),
        )
}
