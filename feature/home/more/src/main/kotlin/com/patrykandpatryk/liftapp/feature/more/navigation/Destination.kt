package com.patrykandpatryk.liftapp.feature.more.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class Destination(
    val navigate: MoreNavigator.() -> Unit,
    @StringRes val titleResourceId: Int,
    @DrawableRes val iconResourceId: Int,
)
