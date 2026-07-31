package com.patrykandpatrick.liftapp.core.preview

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem

object PreviewExercises {
    val exercises =
        listOf(
            RoutineExerciseItem(0L, "Bench Press", "Chest", ExerciseType.Weight, Goal.default),
            RoutineExerciseItem(1L, "Squat", "Legs", ExerciseType.Weight, Goal.default),
            RoutineExerciseItem(2L, "Deadlift", "Back", ExerciseType.Weight, Goal.default),
        )
}
