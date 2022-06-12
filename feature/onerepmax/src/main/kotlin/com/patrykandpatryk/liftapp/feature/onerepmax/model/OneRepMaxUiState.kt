package com.patrykandpatryk.liftapp.feature.onerepmax.model

import android.os.Parcelable
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import kotlinx.parcelize.Parcelize

@Parcelize
data class OneRepMaxUiState(
    val repsInput: String = "",
    val repsInputValid: Boolean = true,
    val reps: Int = 0,
    val massInput: String = "",
    val massInputValid: Boolean = true,
    val mass: Float = 0f,
    val oneRepMax: Float = 0f,
    val massUnit: MassUnit? = null,
    val history: List<HistoryEntryModel> = emptyList(),
) : Parcelable
