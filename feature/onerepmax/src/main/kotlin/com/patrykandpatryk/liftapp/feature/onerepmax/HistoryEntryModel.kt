package com.patrykandpatryk.liftapp.feature.onerepmax

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryEntryModel(
    val reps: Int,
    val mass: String,
    val oneRepMax: String,
    val id: UUID = UUID.randomUUID(),
) : Parcelable
