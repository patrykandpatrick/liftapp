package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.CompositingStrategy
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.liftapp.ui.theme.disabled
import com.patrykandpatrick.liftapp.ui.theme.unfocused

@Composable
fun LiftAppRadioButton(
    selected: Boolean,
    onCheck: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: LiftAppRadioButtonColors = LiftAppRadioButtonDefaults.primaryColors,
    interactionSource: MutableInteractionSource? = null,
) {
    val radioButtonSize = dimens.radioButton.size
    val animatedColor = animateColorAsState(colors.getColor(selected, enabled))
    val innerCircleSize =
        animateDpAsState(
            targetValue = if (selected) 0.dp else radioButtonSize - 4.dp,
            animationSpec =
                if (selected) tween(easing = FastOutSlowInEasing)
                else tween(delayMillis = 100, easing = FastOutSlowInEasing),
        )
    val outerCircleSize =
        animateDpAsState(
                targetValue = radioButtonSize - if (selected) 10.dp else 0.dp,
                animationSpec =
                    if (selected) tween(delayMillis = 100, easing = FastOutSlowInEasing)
                    else tween(easing = FastOutSlowInEasing),
            )
            .value

    Canvas(
        modifier =
            modifier
                .then(
                    if (onCheck != null) {
                        Modifier.selectable(
                            selected = selected,
                            enabled = enabled,
                            role = Role.RadioButton,
                            onClick = onCheck,
                            interactionSource =
                                interactionSource ?: remember { MutableInteractionSource() },
                        )
                    } else {
                        Modifier
                    }
                )
                .defaultMinSize(radioButtonSize, radioButtonSize)
    ) {
        drawContext.graphicsLayer?.compositingStrategy = CompositingStrategy.Offscreen

        drawCircle(color = animatedColor.value, radius = outerCircleSize.toPx() / 2)

        drawCircle(
            color = Color.Black,
            radius = (innerCircleSize.value).toPx() / 2,
            blendMode = BlendMode.DstOut,
        )

        drawCircle(
            color = animatedColor.value,
            radius = (radioButtonSize - 2.dp).toPx() / 2,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

@Immutable
data class LiftAppRadioButtonColors(
    val selected: Color,
    val unselected: Color,
    val selectedDisabled: Color = selected.disabled,
    val unselectedDisabled: Color = unselected.disabled,
) {
    internal fun getColor(selected: Boolean, enabled: Boolean): Color =
        when {
            selected && enabled -> this@LiftAppRadioButtonColors.selected
            !selected && enabled -> unselected
            selected && !enabled -> selectedDisabled
            else -> unselectedDisabled
        }
}

object LiftAppRadioButtonDefaults {

    val primaryColors: LiftAppRadioButtonColors
        @Composable get() = colors()

    val onSurfaceColors: LiftAppRadioButtonColors
        @Composable
        get() =
            colors(
                checkedColor = colorScheme.onSurface,
                uncheckedColor = colorScheme.onSurface.unfocused,
                checkedDisabledColor = colorScheme.onSurfaceVariant.disabled,
                uncheckedDisabledColor = colorScheme.onSurfaceVariant.disabled,
            )

    @Composable
    fun colors(
        checkedColor: Color = colorScheme.primary,
        uncheckedColor: Color = colorScheme.onSurfaceVariant,
        checkedDisabledColor: Color = checkedColor.disabled,
        uncheckedDisabledColor: Color = uncheckedColor.disabled,
    ): LiftAppRadioButtonColors =
        LiftAppRadioButtonColors(
            selected = checkedColor,
            unselected = uncheckedColor,
            selectedDisabled = checkedDisabledColor,
            unselectedDisabled = uncheckedDisabledColor,
        )
}

@ComponentPreview
@Composable
private fun LiftAppRadioButtonPreview() {
    GridPreviewSurface(
        content =
            listOf(
                "Enabled" to
                    {
                        val (selected, setSelected) = remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            LiftAppRadioButton(
                                selected = selected,
                                onCheck = { setSelected(!selected) },
                            )
                            LiftAppRadioButton(
                                selected = !selected,
                                onCheck = { setSelected(!selected) },
                            )
                        }
                    },
                "Disabled" to
                    {
                        val (selected, setSelected) = remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            LiftAppRadioButton(
                                selected = selected,
                                enabled = false,
                                onCheck = { setSelected(!selected) },
                            )
                            LiftAppRadioButton(
                                selected = !selected,
                                enabled = false,
                                onCheck = { setSelected(!selected) },
                            )
                        }
                    },
            )
    )
}
