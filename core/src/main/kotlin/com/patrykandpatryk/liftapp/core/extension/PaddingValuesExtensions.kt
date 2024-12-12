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
