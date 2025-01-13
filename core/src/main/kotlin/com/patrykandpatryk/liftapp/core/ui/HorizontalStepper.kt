package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.Alpha
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape

@Composable
fun StepperItem(
    setIndex: Int,
    selected: Boolean,
    completed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    removeEnabled: Boolean = false,
    onRemoveClick: () -> Unit = {},
    label: (@Composable ColumnScope.(setIndex: Int, enabled: Boolean) -> Unit)? = null,
) {
    val dimens = LocalDimens.current.stepper
    val borderColor =
        animateColorAsState(
            targetValue =
                when {
                    completed && selected ->
                        MaterialTheme.colorScheme.primary.copy(Alpha.get(enabled = enabled))
                    selected ->
                        MaterialTheme.colorScheme.outlineVariant.copy(Alpha.get(enabled = enabled))
                    else -> Color.Transparent
                },
            label = "Border color",
        )

    val backgroundColor =
        animateColorAsState(
            targetValue =
                when {
                    completed ->
                        MaterialTheme.colorScheme.primary.copy(Alpha.get(enabled = enabled))
                    else ->
                        MaterialTheme.colorScheme.outlineVariant.copy(Alpha.get(enabled = enabled))
                },
            label = "Background color",
        )

    val textColor =
        animateColorAsState(
            targetValue =
                when {
                    completed ->
                        MaterialTheme.colorScheme.onPrimary.copy(Alpha.get(enabled = enabled))
                    else ->
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            Alpha.get(enabled = enabled)
                        )
                },
            label = "Text color",
        )

    val borderPadding =
        animateDpAsState(if (selected) dimens.stepBorderPadding else 0.dp, label = "Border padding")

    Column(
        modifier =
            modifier.clickable(
                onClick = onClick,
                interactionSource = null,
                indication = ripple(bounded = false),
                enabled = enabled,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.size(dimens.stepSize)
                    .padding(dimens.stepBorderPadding - borderPadding.value)
                    .drawWithCache {
                        onDrawBehind {
                            drawCircle(
                                color = borderColor.value,
                                radius = (size.minDimension - dimens.stepBorderWidth.toPx()) / 2,
                                style = Stroke(dimens.stepBorderWidth.toPx()),
                            )
                        }
                    }
                    .padding(borderPadding.value)
                    .background(backgroundColor.value, PillShape),
        ) {
            if (completed) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    tint = textColor.value,
                    modifier = Modifier.size(dimens.stepIconSize),
                )
            } else {
                Text(
                    text = (setIndex + 1).toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor.value,
                    textAlign = TextAlign.Center,
                )
            }

            this@Column.AnimatedVisibility(
                visible = removeEnabled,
                modifier =
                    Modifier.offset(6.dp, -6.dp)
                        .clip(PillShape)
                        .clickable(onClick = onRemoveClick)
                        .size(dimens.stepIconSize)
                        .align(Alignment.TopEnd),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier.background(MaterialTheme.colorScheme.surface, PillShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, PillShape)
                            .padding(2.dp),
                )
            }
        }

        label?.invoke(this, setIndex, enabled)
    }
}

@Composable
fun AddStep(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val dimens = LocalDimens.current.stepper
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    Column(
        modifier =
            modifier.clickable(
                onClick = onClick,
                interactionSource = null,
                indication = ripple(bounded = false),
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier.size(dimens.stepSize).padding(dimens.stepBorderPadding).drawWithCache {
                    onDrawBehind {
                        drawCircle(
                            color = borderColor,
                            radius = (size.minDimension - dimens.stepBorderWidth.toPx()) / 2,
                            style =
                                Stroke(
                                    width = dimens.stepBorderWidth.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect =
                                        PathEffect.dashPathEffect(
                                            floatArrayOf(4f.dp.toPx(), 6.5.dp.toPx())
                                        ),
                                ),
                        )
                    }
                },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = stringResource(R.string.action_add_set),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimens.stepIconSize),
            )
        }

        label?.invoke(this)
    }
}

@Composable
fun StepConnector(isCompleted: Boolean, modifier: Modifier = Modifier) {
    val dimens = LocalDimens.current.stepper
    val backgroundColor =
        animateColorAsState(
            targetValue =
                when {
                    isCompleted -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
            label = "Background color",
        )
    Spacer(
        modifier =
            modifier
                .width(dimens.connectorWidth)
                .height(dimens.stepSize)
                .padding(vertical = (dimens.stepSize - dimens.connectorHeight) / 2)
                .background(backgroundColor.value, PillShape)
    )
}

@Composable
fun ColumnScope.StepperItemLabel(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(Alpha.get(enabled = enabled)),
        style = MaterialTheme.typography.labelSmall,
        modifier =
            modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = LocalDimens.current.stepper.stepLabelPadding),
    )
}

@Composable
@LightAndDarkThemePreview
fun StepperItemPreview() {
    LiftAppTheme {
        val label: @Composable ColumnScope.(Int, Boolean) -> Unit = { index, enabled ->
            StepperItemLabel(
                text = stringResource(R.string.exercise_set_set_index, index + 1),
                enabled = enabled,
            )
        }
        Surface {
            val (selectedIndex, setSelectedIndex) = remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.stepper.spacing),
            ) {
                StepperItem(
                    setIndex = 0,
                    selected = selectedIndex == 0,
                    completed = true,
                    onClick = { setSelectedIndex(0) },
                    label = label,
                )
                StepConnector(isCompleted = true)
                StepperItem(
                    setIndex = 1,
                    selected = selectedIndex == 1,
                    completed = true,
                    onClick = { setSelectedIndex(1) },
                    label = label,
                )
                StepConnector(isCompleted = false)
                StepperItem(
                    setIndex = 2,
                    selected = selectedIndex == 3,
                    completed = false,
                    onClick = { setSelectedIndex(3) },
                    label = label,
                )
                StepConnector(isCompleted = false)
                StepperItem(
                    setIndex = 3,
                    selected = selectedIndex == 4,
                    completed = false,
                    onClick = { setSelectedIndex(4) },
                    label = label,
                    enabled = false,
                )
            }
        }
    }
}

@Composable
@LightAndDarkThemePreview
fun StepperItemRemovePreview() {
    LiftAppTheme {
        val label: @Composable ColumnScope.(Int, Boolean) -> Unit = { index, enabled ->
            StepperItemLabel(
                text = stringResource(R.string.exercise_set_set_index, index + 1),
                enabled = enabled,
            )
        }
        val (removeEnabled, setRemoveEnabled) = remember { mutableStateOf(true) }
        Surface {
            val (selectedIndex, setSelectedIndex) = remember { mutableIntStateOf(0) }
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.stepper.spacing),
            ) {
                StepperItem(
                    setIndex = 0,
                    selected = selectedIndex == 0,
                    completed = true,
                    onClick = { setSelectedIndex(0) },
                    label = label,
                    removeEnabled = removeEnabled,
                )
                StepConnector(isCompleted = true)
                StepperItem(
                    setIndex = 1,
                    selected = selectedIndex == 1,
                    completed = true,
                    onClick = { setSelectedIndex(1) },
                    label = label,
                    removeEnabled = removeEnabled,
                )
                StepConnector(isCompleted = false)
                StepperItem(
                    setIndex = 2,
                    selected = selectedIndex == 3,
                    completed = false,
                    onClick = { setSelectedIndex(3) },
                    label = label,
                    removeEnabled = removeEnabled,
                )
                StepConnector(isCompleted = false)
                StepperItem(
                    setIndex = 3,
                    selected = selectedIndex == 4,
                    completed = false,
                    onClick = { setSelectedIndex(4) },
                    label = label,
                    enabled = false,
                    removeEnabled = removeEnabled,
                )
                StepConnector(isCompleted = false)
                AddStep(
                    onClick = { setRemoveEnabled(!removeEnabled) },
                    label = { StepperItemLabel(text = stringResource(R.string.action_add_set)) },
                )
            }
        }
    }
}
