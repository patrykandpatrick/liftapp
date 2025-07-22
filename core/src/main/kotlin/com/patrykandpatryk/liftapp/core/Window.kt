@file:Suppress("SimplifyBooleanWithConstants")

package com.patrykandpatryk.liftapp.core

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

val isCompactWidth: Boolean
    @Composable
    get() =
        currentWindowAdaptiveInfo()
            .windowSizeClass
            .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) == false
