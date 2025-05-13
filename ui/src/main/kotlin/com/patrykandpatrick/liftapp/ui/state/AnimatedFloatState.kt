package com.patrykandpatrick.liftapp.ui.state

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
private fun rememberAnimatedFloatState(
    initialValue: Float = 1f,
    animationSpec: AnimationSpec<Float> = spring(),
): AnimatedFloatState = remember(initialValue) { animatedFloatStateOf(initialValue, animationSpec) }

fun animatedFloatStateOf(
    initialValue: Float = 1f,
    animationSpec: AnimationSpec<Float> = spring(),
): AnimatedFloatState = AnimatedFloatState(initialValue, animationSpec)

class AnimatedFloatState
internal constructor(initialValue: Float, animationSpec: AnimationSpec<Float>) :
    AnimatedState<Float, AnimationVector1D>(initialValue, Float.VectorConverter, animationSpec)
