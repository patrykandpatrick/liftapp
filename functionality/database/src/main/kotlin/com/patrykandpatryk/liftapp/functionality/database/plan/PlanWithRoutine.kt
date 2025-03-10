package com.patrykandpatryk.liftapp.functionality.database.plan

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.goal.GoalEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity

data class PlanWithRoutine(
    @Embedded val plan: PlanEntity,
    @Embedded val routine: RoutineEntity?,
    @Embedded val exercise: ExerciseEntity?,
    @Embedded val goalEntity: GoalEntity?,
    @ColumnInfo(name = "plan_item_order_index") val orderIndex: Int?,
)
