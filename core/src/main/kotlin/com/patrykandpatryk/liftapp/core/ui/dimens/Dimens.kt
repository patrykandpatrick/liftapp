package com.patrykandpatryk.liftapp.core.ui.dimens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val padding: Padding = Padding(),
    val strokeWidth: Dp = 1.dp,
    val verticalItemSpacing: Dp = 16.dp,
) {

    @Immutable
    data class Padding(
        val contentHorizontal: Dp = 16.dp,
        val contentVertical: Dp = 20.dp,
        val itemHorizontal: Dp = 16.dp,
        val itemVertical: Dp = 16.dp,
        val segmentedButtonHorizontal: Dp = 12.dp,
        val segmentedButtonVertical: Dp = 12.dp,
        val segmentedButtonElement: Dp = 8.dp,
    )
}

internal val LocalDimens = staticCompositionLocalOf { Dimens() }

val MaterialTheme.dimens: Dimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDimens.current
