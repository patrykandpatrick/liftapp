package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape

@Composable
fun SegmentedButtonContainer(
    modifier: Modifier = Modifier,
    buttons: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .clip(PillShape)
            .border(
                width = MaterialTheme.dimens.strokeWidth,
                color = MaterialTheme.colorScheme.outline,
                shape = PillShape,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        buttons()
    }
}

@Composable
fun <T> SegmentedButtonContainer(
    items: List<T>,
    modifier: Modifier = Modifier,
    getItem: @Composable RowScope.(Int, T) -> Unit,
) {
    SegmentedButtonContainer(modifier = modifier) {
        items.forEachIndexed { index, item ->
            getItem(index, item)
            if (index < items.lastIndex) {
                VerticalDivider()
            }
        }
    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.outline)
            .fillMaxHeight()
            .width(MaterialTheme.dimens.strokeWidth),
    )
}

@Composable
fun RowScope.SegmentedButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
) {
    Row(
        modifier = modifier
            .background(color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .weight(weight = 1f)
            .fillMaxHeight()
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = if (selected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
            .padding(
                horizontal = MaterialTheme.dimens.padding.segmentedButtonHorizontal,
                vertical = MaterialTheme.dimens.padding.segmentedButtonVertical,
            ),
        horizontalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.dimens.padding.segmentedButtonElement,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {

        val iconPainter = if (selected) painterResource(id = R.drawable.ic_check) else icon
        val tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface

        if (iconPainter != null) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                painter = iconPainter,
                contentDescription = null,
                tint = tint,
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = text,
            color = tint,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
fun PreviewSegmentedButtonWithIcons() {
    LiftAppTheme(darkTheme = false) {
        Surface {

            var selectedIndex by remember { mutableStateOf(1) }

            SegmentedButtonContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

@Preview
@Composable
fun PreviewSegmentedButtonWithNoIcons() {
    LiftAppTheme(darkTheme = true) {
        Surface {

            var selectedIndex by remember { mutableStateOf(1) }

            SegmentedButtonContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
