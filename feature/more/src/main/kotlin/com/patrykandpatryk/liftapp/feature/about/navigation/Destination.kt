package com.patrykandpatryk.liftapp.feature.about.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class Destination(
    val route: String,
    @StringRes val titleResourceId: Int,
    @DrawableRes val iconResourceId: Int,
)
