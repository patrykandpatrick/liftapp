package com.patrykandpatryk.liftapp.feature.onerepmax

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class HistoryEntryModel(
    val reps: Int,
    val mass: String,
    val oneRepMax: String,
    val id: UUID = UUID.randomUUID(),
) : Parcelable
