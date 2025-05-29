package com.patrykandpatryk.liftapp.functionality.database.plan

import androidx.room.Embedded
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.goal.GoalEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity

data class ScheduledRoutine(
    @Embedded val routine: RoutineEntity?,
    @Embedded val exercise: ExerciseEntity?,
    @Embedded val goalEntity: GoalEntity?,
)
