package com.patrykandpatrick.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView(
    value = ROUTINE_WITH_EXERCISE_NAMES_QUERY,
    viewName = ROUTINE_WITH_EXERCISE_NAMES_VIEW,
)
class RoutineWithExerciseNamesView(
    @Embedded val routine: RoutineEntity,
    @ColumnInfo(name = "exercise_names") val exerciseNames: String,
) {

    override fun toString(): String =
        "RoutineWithExercisesView(routine=$routine, exerciseNames=$exerciseNames)"
}

internal const val ROUTINE_WITH_EXERCISE_NAMES_VIEW = "routine_with_exercise_names"

internal const val ROUTINE_WITH_EXERCISE_NAMES_QUERY =
    "SELECT routine_id, routine_name, routine_order_index, " +
        "COALESCE(GROUP_CONCAT(exercise_name, ', '), '') as exercise_names FROM " +
        "(SELECT routine.*, exercise.exercise_name, item.routine_item_order_index, " +
        "membership.routine_item_exercise_order_index FROM routine " +
        "LEFT JOIN routine_item item " +
        "ON routine.routine_id = item.routine_item_routine_id " +
        "LEFT JOIN exercise_with_routine_item membership " +
        "ON membership.routine_item_id = item.routine_item_id " +
        "LEFT JOIN exercise ON exercise.exercise_id = membership.exercise_id " +
        "ORDER BY item.routine_item_routine_id, item.routine_item_order_index, " +
        "membership.routine_item_exercise_order_index) " +
        "GROUP BY routine_id"
