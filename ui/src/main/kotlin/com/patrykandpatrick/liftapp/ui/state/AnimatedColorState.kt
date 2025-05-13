package com.patrykandpatrick.liftapp.ui.state

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces

@Composable
private fun rememberAnimatedColorState(
    initialColor: Color,
    animationSpec: AnimationSpec<Color> = spring(),
): AnimatedColorState =
    remember(initialColor, animationSpec) { animatedColorStateOf(initialColor, animationSpec) }

fun animatedColorStateOf(
    initialColor: Color,
    animationSpec: AnimationSpec<Color> = spring(),
): AnimatedColorState = AnimatedColorState(initialColor, animationSpec)

class AnimatedColorState
internal constructor(initialColor: Color, animationSpec: AnimationSpec<Color>) :
    AnimatedState<Color, AnimationVector4D>(
        initialColor,
        Color.VectorConverter(ColorSpaces.Srgb),
        animationSpec,
    )
