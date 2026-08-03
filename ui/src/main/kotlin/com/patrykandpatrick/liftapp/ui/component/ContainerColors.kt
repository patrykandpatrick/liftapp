package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.theme.disabled

data class ContainerColors(
    val backgroundColor: Color,
    val contentColor: Color,
    val interactiveBorderColors: InteractiveBorderColors,
    val disabledBackgroundColor: Color = backgroundColor.disabled,
    val disabledContentColor: Color = contentColor,
) {
    fun getBackgroundColor(enabled: Boolean): Color =
        if (enabled) backgroundColor else disabledBackgroundColor
}

fun lerp(start: ContainerColors, end: ContainerColors, fraction: Float): ContainerColors =
    ContainerColors(
        backgroundColor = lerpVisibleColor(start.backgroundColor, end.backgroundColor, fraction),
        contentColor = lerpVisibleColor(start.contentColor, end.contentColor, fraction),
        // Borders own their animation. Keeping their target discrete avoids continuously
        // retargeting that animation while the container colors interpolate.
        interactiveBorderColors = end.interactiveBorderColors,
        disabledBackgroundColor =
            lerpVisibleColor(
                start.disabledBackgroundColor,
                end.disabledBackgroundColor,
                fraction,
            ),
        disabledContentColor =
            lerpVisibleColor(start.disabledContentColor, end.disabledContentColor, fraction),
    )

/**
 * Interpolates opacity without introducing transparent black into the visible transition.
 *
 * [Color.Transparent] has black RGB channels. Interpolating it directly with an opaque color
 * therefore produces a dark flash before the intended color appears. Giving a transparent endpoint
 * the other endpoint's RGB channels makes the transition a straightforward fade.
 */
private fun lerpVisibleColor(start: Color, end: Color, fraction: Float): Color {
    val visibleStart = if (start.alpha == 0f) end.copy(alpha = 0f) else start
    val visibleEnd = if (end.alpha == 0f) start.copy(alpha = 0f) else end
    return lerp(visibleStart, visibleEnd, fraction)
}

@Composable
fun animateContainerColorsAsState(
    colors: ContainerColors,
    animationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessLow),
): State<ContainerColors> {
    val oldColors = remember { mutableStateOf(colors) }
    val currentColors = remember { mutableStateOf(colors) }

    LaunchedEffect(colors) {
        if (colors != currentColors.value) {
            oldColors.value = currentColors.value
            animate(initialValue = 0f, targetValue = 1f, animationSpec = animationSpec) {
                value,
                velocity ->
                currentColors.value = lerp(oldColors.value, colors, value)
            }
        }
    }

    return currentColors
}
