package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
@ReadOnlyComposable
fun WindowInsets.toPaddingValues(
    additionalStart: Dp = 0.dp,
    additionalTop: Dp = 0.dp,
    additionalEnd: Dp = 0.dp,
    additionalBottom: Dp = 0.dp,
): PaddingValues {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    val additionalLeft =
        if (layoutDirection == LayoutDirection.Ltr) {
            additionalStart
        } else {
            additionalEnd
        }

    val additionalRight =
        if (layoutDirection == LayoutDirection.Ltr) {
            additionalEnd
        } else {
            additionalStart
        }

    return object : PaddingValues {

        override fun calculateLeftPadding(layoutDirection: LayoutDirection) = with(density) {
            (getLeft(density = this, layoutDirection = layoutDirection).toDp() + additionalLeft).coerceAtLeast(0.dp)
        }

        override fun calculateTopPadding() = with(density) {
            (getTop(density = this).toDp() + additionalTop).coerceAtLeast(0.dp)
        }

        override fun calculateRightPadding(layoutDirection: LayoutDirection) = with(density) {
            (getRight(density = this, layoutDirection = layoutDirection).toDp() + additionalRight).coerceAtLeast(0.dp)
        }

        override fun calculateBottomPadding() = with(density) {
            (getBottom(density = this).toDp() + additionalBottom).coerceAtLeast(0.dp)
        }
    }
}
