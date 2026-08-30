package com.patrykandpatrick.liftapp.functionality.database.plan

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity

data class PlanWithRoutine(
    @Embedded val plan: PlanEntity,
    @Embedded val routine: RoutineEntity?,
    @Embedded val exercise: ExerciseEntity?,
    @ColumnInfo(name = "plan_item_order_index") val orderIndex: Int?,
)
