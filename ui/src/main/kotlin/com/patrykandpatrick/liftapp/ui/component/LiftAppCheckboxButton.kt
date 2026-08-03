package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.liftapp.ui.theme.disabled

@Composable
fun LiftAppCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: LiftAppCheckboxColors = LiftAppCheckboxDefaults.primaryColors,
    interactionSource: MutableInteractionSource? = null,
) {
    val checkboxSize = dimens.checkbox.size
    val checkboxCornerSize = dimens.checkbox.cornerSize
    val strokeWidth = dimens.checkbox.strokeWidth
    val animatedColor = animateColorAsState(colors.getColor(checked, enabled))
    val animatedCheckmarkColor =
        animateColorAsState(if (enabled) colors.checkmark else colors.checkmarkDisabled)
    val fillRectSize = remember { mutableStateOf(if (checked) checkboxSize else 0.dp) }
    val path = remember { Path() }
    val pathMeasure = remember { PathMeasure() }
    val pathProgress = remember { mutableFloatStateOf(if (checked) 1f else 0f) }

    LaunchedEffect(checked) {
        if (checked) {
            animate(
                initialValue = fillRectSize.value,
                targetValue = checkboxSize,
                typeConverter = Dp.VectorConverter,
            ) { value, velocity ->
                fillRectSize.value = value
            }

            animate(initialValue = pathProgress.floatValue, targetValue = 1f) { value, velocity ->
                pathProgress.floatValue = value
            }
        } else {
            animate(initialValue = pathProgress.floatValue, targetValue = 0f) { value, velocity ->
                pathProgress.floatValue = value
            }

            animate(
                initialValue = fillRectSize.value,
                targetValue = 0.dp,
                typeConverter = Dp.VectorConverter,
            ) { value, velocity ->
                fillRectSize.value = value
            }
        }
    }

    Canvas(
        modifier =
            modifier
                .then(
                    if (onCheckedChange != null) {
                        Modifier.selectable(
                            selected = checked,
                            enabled = enabled,
                            role = Role.Checkbox,
                            onClick = { onCheckedChange(!checked) },
                            interactionSource =
                                interactionSource ?: remember { MutableInteractionSource() },
                        )
                    } else {
                        Modifier
                    }
                )
                .defaultMinSize(checkboxSize, checkboxSize)
    ) {
        val checkboxSizePx = checkboxSize.toPx()
        val checkboxTopLeft =
            Offset(
                size.width / 2 - checkboxSizePx / 2,
                size.height / 2 - checkboxSizePx / 2,
            )
        val fillSizePx = fillRectSize.value.toPx()
        if (fillSizePx > 0f) {
            val fillCornerRadius = minOf(checkboxCornerSize.toPx(), fillSizePx / 2)
            drawRoundRect(
                color = animatedColor.value,
                topLeft =
                    Offset(
                        size.width / 2 - fillSizePx / 2,
                        size.height / 2 - fillSizePx / 2,
                    ),
                size = Size(fillSizePx, fillSizePx),
                cornerRadius = CornerRadius(fillCornerRadius, fillCornerRadius),
            )
        }

        if (fillSizePx < checkboxSizePx) {
            drawRoundRect(
                color = animatedColor.value,
                topLeft = checkboxTopLeft,
                size = Size(checkboxSizePx, checkboxSizePx),
                cornerRadius = CornerRadius(checkboxCornerSize.toPx(), checkboxCornerSize.toPx()),
                style = Stroke(width = strokeWidth.toPx()),
            )
        }

        path.rewind()
        path.moveTo(size.center.x - 6.dp.toPx(), size.center.y)
        path.lineTo(size.center.x - 2.dp.toPx(), size.center.y + 4.dp.toPx())
        path.lineTo(size.center.x + 6.dp.toPx(), size.center.y - 4.dp.toPx())
        pathMeasure.setPath(path, false)

        drawPath(
            path = path,
            color = animatedCheckmarkColor.value,
            style =
                Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect =
                        PathEffect.dashPathEffect(
                            floatArrayOf(pathMeasure.length * pathProgress.floatValue, 100f)
                        ),
                ),
        )
    }
}

@Immutable
data class LiftAppCheckboxColors(
    val selected: Color,
    val unselected: Color,
    val checkmark: Color,
    val selectedDisabled: Color = selected.disabled,
    val unselectedDisabled: Color = unselected.disabled,
    val checkmarkDisabled: Color = checkmark.disabled,
) {
    internal fun getColor(selected: Boolean, enabled: Boolean): Color =
        when {
            selected && enabled -> this@LiftAppCheckboxColors.selected
            !selected && enabled -> unselected
            selected && !enabled -> selectedDisabled
            else -> unselectedDisabled
        }
}

object LiftAppCheckboxDefaults {

    val primaryColors: LiftAppCheckboxColors
        @Composable get() = colors()

    @Composable
    fun colors(
        checkedColor: Color = colorScheme.primary,
        uncheckedColor: Color = colorScheme.foregroundVariant,
        checkmarkColor: Color = colorScheme.onPrimary,
        checkedDisabledColor: Color = checkedColor.disabled,
        uncheckedDisabledColor: Color = uncheckedColor.disabled,
    ): LiftAppCheckboxColors =
        LiftAppCheckboxColors(
            selected = checkedColor,
            unselected = uncheckedColor,
            checkmark = checkmarkColor,
            selectedDisabled = checkedDisabledColor,
            unselectedDisabled = uncheckedDisabledColor,
        )
}

@ComponentPreview
@Composable
private fun LiftAppCheckboxPreview() {
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
                            LiftAppCheckbox(
                                checked = selected,
                                onCheckedChange = { setSelected(!selected) },
                            )
                            LiftAppCheckbox(
                                checked = !selected,
                                onCheckedChange = { setSelected(!selected) },
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
                            LiftAppCheckbox(
                                checked = selected,
                                enabled = false,
                                onCheckedChange = { setSelected(!selected) },
                            )
                            LiftAppCheckbox(
                                checked = !selected,
                                enabled = false,
                                onCheckedChange = { setSelected(!selected) },
                            )
                        }
                    },
            )
    )
}
