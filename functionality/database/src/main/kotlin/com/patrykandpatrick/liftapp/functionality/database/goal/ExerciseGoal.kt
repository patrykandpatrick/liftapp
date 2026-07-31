package com.patrykandpatrick.liftapp.functionality.database.goal

import androidx.room.ColumnInfo
import com.patrykandpatrick.liftapp.domain.goal.Goal

class ExerciseGoal(@ColumnInfo(name = "exercise_goal") val goal: Goal)
