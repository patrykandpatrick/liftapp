package com.patrykandpatryk.liftapp.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class NavItemRoute<T: Any>(
    val route: T,
    @StringRes val titleRes: Int,
    @DrawableRes val deselectedIconRes: Int,
    @DrawableRes val selectedIconRes: Int = deselectedIconRes,
)
