package com.patrykandpatryk.liftapp.core.preview

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem

object PreviewExercises {
    val exercises =
        listOf(
            RoutineExerciseItem(0L, "Bench Press", "Chest", ExerciseType.Weight, Goal.default),
            RoutineExerciseItem(1L, "Squat", "Legs", ExerciseType.Weight, Goal.default),
            RoutineExerciseItem(2L, "Deadlift", "Back", ExerciseType.Weight, Goal.default),
        )
}
