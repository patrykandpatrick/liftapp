package com.patrykandpatrick.feature.exercisegoal.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.domain.model.Name

@Stable
data class State(
    val goalID: Long,
    val exerciseName: Name,
    val input: GoalInput,
    val goalInfoVisible: Boolean,
    val goalSaved: Boolean,
)
