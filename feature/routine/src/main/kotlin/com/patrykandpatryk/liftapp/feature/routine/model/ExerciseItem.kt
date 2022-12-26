package com.patrykandpatryk.liftapp.feature.routine.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class ExerciseItem(
    val id: Long,
    val name: String,
    val muscles: String,
    @DrawableRes val iconRes: Int,
) : Parcelable
