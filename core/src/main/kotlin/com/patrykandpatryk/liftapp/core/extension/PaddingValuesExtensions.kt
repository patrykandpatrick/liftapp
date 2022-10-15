package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp

@Composable
fun PaddingValues.calculateStartPadding(): Dp =
    calculateStartPadding(LocalLayoutDirection.current)

@Composable
fun PaddingValues.calculateEndPadding(): Dp =
    calculateEndPadding(LocalLayoutDirection.current)
