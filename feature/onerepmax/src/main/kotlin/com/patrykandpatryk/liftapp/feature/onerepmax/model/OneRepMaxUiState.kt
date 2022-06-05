package com.patrykandpatryk.liftapp.feature.onerepmax.model

import com.patrykandpatryk.liftapp.domain.unit.MassUnit

data class OneRepMaxUiState(
    val repsInput: String,
    val repsInputValid: Boolean,
    val reps: Int,
    val massInput: String,
    val massInputValid: Boolean,
    val mass: Float,
    val oneRepMax: Float,
    val massUnit: MassUnit?,
)
