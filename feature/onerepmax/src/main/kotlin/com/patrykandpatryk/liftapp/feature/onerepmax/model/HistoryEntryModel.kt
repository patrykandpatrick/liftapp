package com.patrykandpatryk.liftapp.feature.onerepmax.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryEntryModel(
    val reps: Int,
    val mass: Float,
    val oneRepMax: Float,
) : Parcelable
