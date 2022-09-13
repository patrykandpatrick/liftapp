package com.patrykandpatryk.liftapp.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry

@Immutable
data class NavItemRoute(
    val route: String,
    @StringRes val titleRes: Int,
    @DrawableRes val deselectedIconRes: Int,
    @DrawableRes val selectedIconRes: Int = deselectedIconRes,
    val content: @Composable (
        entry: NavBackStackEntry,
        modifier: Modifier,
        padding: PaddingValues,
        navigate: (String) -> Unit,
    ) -> Unit,
)
