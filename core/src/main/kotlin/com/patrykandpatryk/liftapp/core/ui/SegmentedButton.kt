package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape

@Composable
fun SegmentedButtonContainer(
    modifier: Modifier = Modifier,
    shape: Shape = PillShape,
    buttons: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            modifier
                .height(IntrinsicSize.Max)
                .clip(shape)
                .border(
                    width = MaterialTheme.dimens.strokeWidth,
                    color = MaterialTheme.colorScheme.outline,
                    shape = shape,
                ),
        verticalAlignment = Alignment.CenterVertically,
        content = buttons,
    )
}

@Composable
fun <T> SegmentedButtonContainer(
    items: List<T>,
    modifier: Modifier = Modifier,
    shape: Shape = PillShape,
    getItem: @Composable RowScope.(Int, T) -> Unit,
) {
    SegmentedButtonContainer(modifier = modifier, shape = shape) {
        items.forEachIndexed { index, item ->
            getItem(index, item)
            if (index < items.lastIndex) {
                VerticalDivider()
            }
        }
    }
}

@Composable
fun VerticalSegmentedButtonContainer(
    modifier: Modifier = Modifier,
    shape: Shape = PillShape,
    buttons: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
            modifier
                .width(IntrinsicSize.Max)
                .clip(shape)
                .border(
                    width = MaterialTheme.dimens.strokeWidth,
                    color = MaterialTheme.colorScheme.outline,
                    shape = shape,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = buttons,
    )
}

@Composable
fun <T> VerticalSegmentedButtonContainer(
    items: List<T>,
    modifier: Modifier = Modifier,
    shape: Shape = PillShape,
    getItem: @Composable ColumnScope.(Int, T) -> Unit,
) {
    VerticalSegmentedButtonContainer(modifier = modifier, shape = shape) {
        items.forEachIndexed { index, item ->
            getItem(index, item)
            if (index < items.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .background(color = MaterialTheme.colorScheme.outline)
                .fillMaxHeight()
                .width(MaterialTheme.dimens.strokeWidth)
    )
}

@Composable
fun RowScope.SegmentedButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    showIcon: Boolean = true,
) {
    com.patrykandpatryk.liftapp.core.ui.SegmentedButton(
        text = text,
        onClick = onClick,
        selected = selected,
        icon = icon,
        modifier = modifier.fillMaxHeight().weight(weight = 1f),
        showIcon = showIcon,
    )
}

@Composable
fun ColumnScope.SegmentedButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    showIcon: Boolean = icon != null,
) {
    com.patrykandpatryk.liftapp.core.ui.SegmentedButton(
        text = text,
        onClick = onClick,
        selected = selected,
        icon = icon,
        modifier = modifier.fillMaxWidth().weight(weight = 1f, fill = false),
        showIcon = showIcon,
    )
}

@Composable
private fun SegmentedButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean,
    showIcon: Boolean,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {

    Row(
        modifier =
            modifier
                .background(
                    color =
                        if (selected) MaterialTheme.colorScheme.secondaryContainer
                        else Color.Transparent
                )
                .clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication =
                        ripple(
                            color =
                                if (selected) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                        ),
                )
                .padding(
                    horizontal = MaterialTheme.dimens.padding.segmentedButtonHorizontal,
                    vertical = MaterialTheme.dimens.padding.segmentedButtonVertical,
                ),
        horizontalArrangement =
            Arrangement.spacedBy(
                space = MaterialTheme.dimens.padding.segmentedButtonElement,
                alignment = Alignment.CenterHorizontally,
            ),
    ) {
        val iconPainter =
            when {
                showIcon.not() -> null
                selected -> painterResource(id = R.drawable.ic_check)
                else -> icon
            }

        val tint =
            if (selected) MaterialTheme.colorScheme.onSecondaryContainer
            else MaterialTheme.colorScheme.onSurface

        if (iconPainter != null) {
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                painter = iconPainter,
                contentDescription = null,
                tint = tint,
            )
        }

        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = text,
            color = tint,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewSegmentedButtonWithIcons() {
    LiftAppTheme {
        Surface {
            var selectedIndex by remember { mutableStateOf(1) }

            SegmentedButtonContainer(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                items = listOf("First Item", "Second Item", "Third Item"),
            ) { index, text ->
                SegmentedButton(
                    text = text,
                    onClick = { selectedIndex = index },
                    selected = selectedIndex == index,
                    icon = painterResource(id = R.drawable.ic_info),
                )
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewSegmentedButtonWithNoIcons() {
    LiftAppTheme {
        Surface {
            var selectedIndex by remember { mutableStateOf(1) }

            SegmentedButtonContainer(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                items = listOf("First Item", "Second Item"),
            ) { index, text ->
                SegmentedButton(
                    text = text,
                    onClick = { selectedIndex = index },
                    selected = selectedIndex == index,
                )
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewVerticalSegmentedButtonWithNoIcons() {
    LiftAppTheme {
        Surface {
            var selectedIndex by remember { mutableStateOf(1) }

            VerticalSegmentedButtonContainer(
                modifier = Modifier.padding(16.dp),
                items = listOf("First Item", "Second Item"),
            ) { index, text ->
                SegmentedButton(
                    text = text,
                    onClick = { selectedIndex = index },
                    selected = selectedIndex == index,
                )
            }
        }
    }
}
