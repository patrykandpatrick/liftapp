package com.patrykandpatryk.liftapp.feature.exercise.model

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.core.model.MuscleModel

@Immutable
data class State(
    val name: String,
    val showDeleteDialog: Boolean,
    val imagePath: String?,
    val muscles: List<MuscleModel>,
)
