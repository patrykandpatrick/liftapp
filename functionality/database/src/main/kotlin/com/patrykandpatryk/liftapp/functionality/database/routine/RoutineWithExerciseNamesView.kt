package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    value = "SELECT routine_id, routine_name, GROUP_CONCAT(exercise_name, ', ') as exercise_names from " +
        "(SELECT routine.*, exercise.exercise_name , ewr.order_index FROM exercise_with_routine ewr " +
        "LEFT JOIN routine ON routine.routine_id = ewr.routine_id " +
        "LEFT JOIN exercise ON exercise.exercise_id = ewr.exercise_id " +
        "ORDER BY ewr.routine_id, ewr.order_index) " +
        "GROUP BY routine_id",
    viewName = "routine_with_exercise_names",
)
class RoutineWithExerciseNamesView(
    @Embedded val routine: RoutineEntity,
    @ColumnInfo(name = "exercise_names") val exerciseNames: String,
) {

    override fun toString(): String =
        "RoutineWithExercisesView(routine=$routine, exerciseNames=$exerciseNames)"
}
