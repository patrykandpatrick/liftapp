package com.patrykandpatryk.liftapp.functionality.database.goal

import androidx.room.ColumnInfo
import com.patrykandpatryk.liftapp.domain.goal.Goal

class ExerciseGoal(
    @ColumnInfo(name = "exercise_goal")
    val goal: Goal
)
