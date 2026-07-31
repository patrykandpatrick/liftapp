package com.patrykandpatrick.liftapp.functionality.database.plan

import androidx.room.Embedded
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.goal.GoalEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity

data class ScheduledRoutine(
    @Embedded val routine: RoutineEntity?,
    @Embedded val exercise: ExerciseEntity?,
    @Embedded val goalEntity: GoalEntity?,
)
