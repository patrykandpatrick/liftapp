package com.patrykandpatryk.liftapp.core.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.ui.unit.IntOffset

const val ENTER_ANIM_DURATION = 300
const val EXIT_ANIM_DURATION = 200

private const val ENTER_TRANSITION_SPRING_DAMPING_RATIO = 0.55f
private const val ENTER_TRANSITION_FADE_HEIGHT_DIVIDER = 8
private const val ENTER_TRANSITION_SLIDE_WIDTH_DIVIDER = 8f

private const val SLIDE_DURATION = 400
private const val SLIDE_FADE_DURATION = 175

fun slideAndFadeIn(
    fadeDuration: Int = ENTER_ANIM_DURATION,
    springDampingRatio: Float = ENTER_TRANSITION_SPRING_DAMPING_RATIO,
    fadeHeightDivider: Int = ENTER_TRANSITION_FADE_HEIGHT_DIVIDER,
): EnterTransition =
    slideIn(animationSpec = spring(dampingRatio = springDampingRatio)) { fullSize ->
        IntOffset(x = 0, y = fullSize.height / fadeHeightDivider)
    } + fadeIn(animationSpec = tween(fadeDuration))

fun sharedXAxisEnterTransition(
    forward: Boolean = true,
    slideWidthDivider: Float = ENTER_TRANSITION_SLIDE_WIDTH_DIVIDER,
): EnterTransition =
    slideIn(animationSpec = tween(durationMillis = SLIDE_DURATION, easing = EaseInOut)) {
        IntOffset(x = (it.width / slideWidthDivider).toInt() * if (forward) 1 else -1, y = 0)
    } +
        fadeIn(
            animationSpec =
                tween(
                    delayMillis = SLIDE_FADE_DURATION,
                    durationMillis = SLIDE_FADE_DURATION,
                    easing = EaseIn,
                )
        )

fun sharedXAxisExitTransition(
    forward: Boolean = true,
    slideWidthDivider: Float = ENTER_TRANSITION_SLIDE_WIDTH_DIVIDER,
): ExitTransition =
    slideOut(animationSpec = tween(SLIDE_DURATION, easing = EaseInOut)) {
        IntOffset(x = (it.width / slideWidthDivider).toInt() * if (forward) -1 else 1, y = 0)
    } + fadeOut(animationSpec = tween(durationMillis = SLIDE_FADE_DURATION, easing = EaseOut))
