package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
@ReadOnlyComposable
fun PaddingValues.calculateStartPadding(): Dp = calculateStartPadding(LocalLayoutDirection.current)

@Composable
@ReadOnlyComposable
fun PaddingValues.calculateEndPadding(): Dp = calculateEndPadding(LocalLayoutDirection.current)

fun PaddingValues.horizontal(): Dp =
    calculateStartPadding(LayoutDirection.Ltr) + calculateEndPadding(LayoutDirection.Ltr)

fun PaddingValues.vertical(): Dp = calculateTopPadding() + calculateBottomPadding()

@Composable
@ReadOnlyComposable
fun WindowInsets.getBottom(): Dp = asPaddingValues().calculateBottomPadding()

@Composable
fun PaddingValues.copy(
    start: Dp = calculateStartPadding(),
    top: Dp = calculateTopPadding(),
    end: Dp = calculateEndPadding(),
    bottom: Dp = calculateBottomPadding(),
): PaddingValues = PaddingValues(start = start, top = top, end = end, bottom = bottom)

@Composable
fun PaddingValues.increaseBy(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): PaddingValues =
    copy(
        start = calculateStartPadding() + start,
        top = calculateTopPadding() + top,
        end = calculateEndPadding() + end,
        bottom = calculateBottomPadding() + bottom,
    )

@Composable
fun PaddingValues.increaseBy(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues =
    increaseBy(start = horizontal, top = vertical, end = horizontal, bottom = vertical)

@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues =
    PaddingValues(
        start = calculateStartPadding() + other.calculateStartPadding(),
        top = calculateTopPadding() + other.calculateTopPadding(),
        end = calculateEndPadding() + other.calculateEndPadding(),
        bottom = calculateBottomPadding() + other.calculateBottomPadding(),
    )
