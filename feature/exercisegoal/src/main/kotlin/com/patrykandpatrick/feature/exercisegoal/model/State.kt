package com.patrykandpatrick.feature.exercisegoal.model

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.model.Name

@Stable
data class State(
    val goal: Goal,
    val exerciseName: Name,
    val input: GoalInput,
    val goalInfoVisible: Boolean,
)
