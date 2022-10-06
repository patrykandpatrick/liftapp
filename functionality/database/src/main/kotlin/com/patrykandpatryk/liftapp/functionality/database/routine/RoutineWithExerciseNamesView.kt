package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    value = "SELECT routine.*, GROUP_CONCAT(exercise.name, ', ') AS exercise_names FROM exercise_with_routine ewr " +
        "LEFT JOIN routine ON routine.id = ewr.routine_id " +
        "LEFT JOIN exercise ON exercise.id = ewr.exercise_id " +
        "GROUP BY routine.id",
    viewName = "routine_with_exercise_names",
)
class RoutineWithExerciseNamesView(
    @Embedded val routine: RoutineEntity,
    @ColumnInfo(name = "exercise_names") val exerciseNames: String,
) {

    override fun toString(): String =
        "RoutineWithExercisesView(routine=$routine, exerciseNames=$exerciseNames)"
}
