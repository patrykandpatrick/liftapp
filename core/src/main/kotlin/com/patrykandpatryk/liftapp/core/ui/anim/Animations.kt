package com.patrykandpatryk.liftapp.core.ui.anim

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.ui.unit.IntOffset

const val ENTER_ANIM_DURATION = 300
const val EXIT_ANIM_DURATION = 200

private const val ENTER_TRANSITION_SPRING_DAMPING_RATIO = 0.55f
private const val ENTER_TRANSITION_FADE_HEIGHT_DIVIDER = 8

fun slideAndFadeIn(
    fadeDuration: Int = ENTER_ANIM_DURATION,
    springDampingRatio: Float = ENTER_TRANSITION_SPRING_DAMPING_RATIO,
    fadeHeightDivider: Int = ENTER_TRANSITION_FADE_HEIGHT_DIVIDER,
): EnterTransition =
    slideIn(
        animationSpec = spring(dampingRatio = springDampingRatio),
    ) { fullSize ->
        IntOffset(x = 0, y = fullSize.height / fadeHeightDivider)
    } + fadeIn(
        animationSpec = tween(fadeDuration),
    )
