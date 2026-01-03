package com.patrykandpatryk.liftapp.core.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import kotlin.reflect.KType

@Immutable
data class NavItemRoute<T : Any>(
    val route: T,
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val content: @Composable (Modifier) -> Unit,
    val typeMap: Map<KType, NavType<*>> = emptyMap(),
)
