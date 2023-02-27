package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

val Dp.pixels: Float
    @Composable get() = value * LocalDensity.current.density
