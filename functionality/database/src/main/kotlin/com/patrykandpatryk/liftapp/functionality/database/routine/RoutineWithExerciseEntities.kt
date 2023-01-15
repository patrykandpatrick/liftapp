package com.patrykandpatryk.liftapp.functionality.database.routine

import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

class RoutineWithExerciseEntities(
    val routine: RoutineEntity,
    val exercises: List<ExerciseEntity>,
) {

    operator fun component1(): RoutineEntity = routine

    operator fun component2(): List<ExerciseEntity> = exercises
}
