package com.patrykandpatryk.liftapp.core.ui.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

val Spring.StiffnessForAppearance
    get() = 2_000f

fun <T> visibilityChangeSpring(visibilityThreshold: T? = null) =
    spring<T>(
        dampingRatio = .65f,
        stiffness = Spring.StiffnessMediumLow,
        visibilityThreshold = visibilityThreshold,
    )
