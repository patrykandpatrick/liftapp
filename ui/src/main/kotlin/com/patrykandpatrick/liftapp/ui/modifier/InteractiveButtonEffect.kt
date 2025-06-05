package com.patrykandpatrick.liftapp.ui.modifier

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.interaction.extendedInteractions

fun Modifier.interactiveButtonEffect(
    colors: InteractiveBorderColors,
    onClick: () -> Unit,
    borderWidth: Dp = 1.dp,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    indicationScale: IndicationScale = IndicationScale(),
    role: Role? = null,
    scaleAnimationSpec: AnimationSpec<Float> =
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
    colorAnimationSpec: AnimationSpec<Color> = spring(),
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()

    clickable(
            interactionSource = null,
            indication = null,
            enabled = enabled,
            role = role,
            onClick = onClick,
        )
        .extendedInteractions(
            enabled = enabled,
            interactionSource = interactionSource,
            coroutineScope = scope,
        )
        .interactiveScale(
            interactionSource = interactionSource,
            animationSpec = scaleAnimationSpec,
            scale = indicationScale,
        )
        .interactiveBorder(
            interactionSource = interactionSource,
            colors = colors,
            width = borderWidth,
            shape = shape,
            animationSpec = colorAnimationSpec,
        )
}
