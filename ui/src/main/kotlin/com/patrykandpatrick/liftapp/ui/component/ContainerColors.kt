package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.animation.core.AnimationSpec
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
import com.patrykandpatrick.liftapp.ui.lerp

data class ContainerColors(
    val backgroundColor: Color,
    val contentColor: Color,
    val interactiveBorderColors: InteractiveBorderColors,
    val disabledBackgroundColor: Color,
    val disabledContentColor: Color,
)

fun lerp(start: ContainerColors, end: ContainerColors, fraction: Float): ContainerColors =
    ContainerColors(
        backgroundColor = lerp(start.backgroundColor, end.backgroundColor, fraction),
        contentColor = lerp(start.contentColor, end.contentColor, fraction),
        interactiveBorderColors =
            lerp(start.interactiveBorderColors, end.interactiveBorderColors, fraction),
        disabledBackgroundColor =
            lerp(start.disabledBackgroundColor, end.disabledBackgroundColor, fraction),
        disabledContentColor = lerp(start.disabledContentColor, end.disabledContentColor, fraction),
    )

@Composable
fun animateContainerColorsAsState(
    colors: ContainerColors,
    animationSpec: AnimationSpec<Float> = spring(),
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
