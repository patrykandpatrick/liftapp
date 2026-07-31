package com.patrykandpatrick.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.goal.GoalEntity

data class RoutineItemExerciseDto(
    @Embedded val item: RoutineItemEntity,
    @Embedded val exercise: ExerciseEntity,
    @Embedded val goal: GoalEntity?,
    @ColumnInfo(name = "routine_item_exercise_order") val exerciseOrder: Int,
    @ColumnInfo(name = "superset_sets") val supersetSets: Int?,
    @ColumnInfo(name = "superset_rest_time_millis") val supersetRestTimeMillis: Long?,
)
