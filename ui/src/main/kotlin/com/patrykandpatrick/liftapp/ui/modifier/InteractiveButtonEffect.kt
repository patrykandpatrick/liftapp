package com.patrykandpatrick.liftapp.ui.modifier

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
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
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    borderWidth: Dp = 1.dp,
    borderHorizontalInset: Dp = 0.dp,
    maxBorderWidth: Dp? = null,
    maxBorderHeight: Dp? = null,
    enabled: Boolean = true,
    checked: Boolean = false,
    shape: Shape = RectangleShape,
    indicationScale: IndicationScale = IndicationScale(),
    role: Role? = null,
    scaleAnimationSpec: AnimationSpec<Float> =
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
    colorAnimationSpec: AnimationSpec<Color> = spring(stiffness = Spring.StiffnessLow),
    interactionSource: MutableInteractionSource? = null,
): Modifier = composed {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    then(
            if (onClick == null && onLongClick == null) {
                Modifier
            } else if (onLongClick == null) {
                Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = enabled,
                    role = role,
                    onClick = checkNotNull(onClick),
                )
            } else {
                Modifier.combinedClickable(
                    interactionSource = null,
                    indication = null,
                    enabled = enabled,
                    role = role,
                    onLongClick = onLongClick,
                    onClick = onClick ?: {},
                )
            }
        )
        .extendedInteractions(
            enabled = enabled && (onClick != null || onLongClick != null),
            interactionSource = interactionSource,
        )
        .interactiveButtonVisualEffect(
            colors = colors,
            interactionSource = interactionSource,
            borderWidth = borderWidth,
            borderHorizontalInset = borderHorizontalInset,
            maxBorderWidth = maxBorderWidth,
            maxBorderHeight = maxBorderHeight,
            checked = checked,
            shape = shape,
            indicationScale = indicationScale,
            scaleAnimationSpec = scaleAnimationSpec,
            colorAnimationSpec = colorAnimationSpec,
        )
}

fun Modifier.interactiveButtonVisualEffect(
    colors: InteractiveBorderColors,
    interactionSource: InteractionSource,
    borderWidth: Dp = 1.dp,
    borderHorizontalInset: Dp = 0.dp,
    maxBorderWidth: Dp? = null,
    maxBorderHeight: Dp? = null,
    checked: Boolean = false,
    shape: Shape = RectangleShape,
    indicationScale: IndicationScale = IndicationScale(),
    scaleAnimationSpec: AnimationSpec<Float> =
        spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
    colorAnimationSpec: AnimationSpec<Color> = spring(stiffness = Spring.StiffnessLow),
): Modifier =
    this.interactiveScale(
            interactionSource = interactionSource,
            animationSpec = scaleAnimationSpec,
            scale = indicationScale,
        )
        .interactiveBorder(
            interactionSource = interactionSource,
            colors = colors,
            width = borderWidth,
            horizontalInset = borderHorizontalInset,
            shape = shape,
            checked = checked,
            animationSpec = colorAnimationSpec,
            maxWidth = maxBorderWidth,
            maxHeight = maxBorderHeight,
        )
