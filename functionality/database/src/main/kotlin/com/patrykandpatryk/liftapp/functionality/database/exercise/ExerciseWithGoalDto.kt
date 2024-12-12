package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.room.Embedded
import com.patrykandpatryk.liftapp.functionality.database.goal.GoalEntity

data class ExerciseWithGoalDto(
    @Embedded val exerciseEntity: ExerciseEntity,
    @Embedded val goalEntity: GoalEntity?,
)
