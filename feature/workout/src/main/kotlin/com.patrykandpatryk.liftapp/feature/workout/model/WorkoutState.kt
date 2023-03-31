package com.patrykandpatryk.liftapp.feature.workout.model

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET

@Immutable
data class WorkoutState(
    val id: Long = ID_NOT_SET,
)
