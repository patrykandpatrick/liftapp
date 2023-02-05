package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.scaleCornerSize
import com.patrykandpatryk.liftapp.core.extension.thenIfNotNull
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape
import com.patrykandpatryk.liftapp.domain.extension.length

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    iconPainter: Painter? = null,
    enabled: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        title = title,
        modifier = modifier,
        description = description,
        trailing = trailing,
        icon = {
            if (iconPainter != null) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = PillShape,
                        )
                        .padding(8.dp),
                    painter = iconPainter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        onClick = onClick,
        actions = actions,
        enabled = enabled,
        paddingValues = paddingValues,
        titleHighlightPosition = titleHighlightPosition,
    )
}

@Composable
fun CheckableListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    iconPainter: Painter? = null,
    checked: Boolean,
    enabled: Boolean = true,
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    onCheckedChange: (Boolean) -> Unit,
) {

    val animationFraction by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(),
    )

    val shape = MaterialTheme.shapes.small.scaleCornerSize(animationFraction)

    ListItem(
        title = title,
        modifier = modifier
            .border(
                width = MaterialTheme.dimens.strokeWidth,
                color = MaterialTheme.colorScheme.primary.copy(alpha = animationFraction),
                shape = shape,
            )
            .clip(shape),
        description = description,
        trailing = trailing,
        icon = {
            if (iconPainter != null) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = PillShape,
                        )
                        .padding(8.dp),
                    painter = iconPainter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        actions = {
            Checkbox(
                checked = checked,
                onCheckedChange = if (enabled) onCheckedChange else null,
            )
        },
        onClick = { onCheckedChange(checked.not()) },
        enabled = enabled,
        paddingValues = paddingValues,
        titleHighlightPosition = titleHighlightPosition,
    )
}

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    icon: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    enabled: Boolean = true,
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontal),
        modifier = modifier
            .alpha(if (enabled) 1f else ContentAlpha.disabled)
            .fillMaxWidth()
            .thenIfNotNull(value = onClick) { clickable(onClick = it, enabled = enabled) }
            .padding(paddingValues),
    ) {

        icon()

        Column(modifier = Modifier.weight(1f)) {

            if (!titleHighlightPosition.isEmpty()) {
                var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                val highlightColor = MaterialTheme.colorScheme.tertiaryContainer
                val highlightCornerRadiusPx =
                    with(LocalDensity.current) { MaterialTheme.dimens.list.itemTitleHighlightCornerRadius.toPx() }
                ListItemTitle(
                    text = title,
                    modifier = Modifier.drawBehind {
                        textLayoutResult?.run {
                            titleHighlightPosition
                                .take(titleHighlightPosition.length)
                                .map { getBoundingBox(it) }
                                .groupBy { it.bottom }
                                .forEach { (_, boundingBoxes) ->
                                    val boundingBox = boundingBoxes.first().copy(right = boundingBoxes.last().right)
                                    drawRoundRect(
                                        highlightColor,
                                        boundingBox.topLeft,
                                        boundingBox.size,
                                        CornerRadius(highlightCornerRadiusPx),
                                    )
                                }
                        }
                    },
                    spanStyles = listOf(
                        AnnotatedString.Range(
                            SpanStyle(MaterialTheme.colorScheme.onTertiaryContainer),
                            titleHighlightPosition.first,
                            titleHighlightPosition.last,
                        ),
                    ),
                    onTextLayout = { textLayoutResult = it },
                )
            } else {
                ListItemTitle(title)
            }

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        actions()
    }
}

@Composable
private fun ListItemTitle(
    text: String,
    modifier: Modifier = Modifier,
    spanStyles: List<AnnotatedString.Range<SpanStyle>> = emptyList(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    Text(
        text = AnnotatedString(text, spanStyles),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        onTextLayout = onTextLayout,
        style = MaterialTheme.typography.titleMedium,
    )
}

object ListItemDefaults {

    val paddingValues: PaddingValues
        @Composable get() = PaddingValues(
            vertical = LocalDimens.current.padding.itemVertical,
            horizontal = LocalDimens.current.padding.contentHorizontal,
        )
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleItem() {
    LiftAppTheme {
        Surface {
            ListItem(title = "This is a title")
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithDescItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                description = "This is a description",
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithDescAndIconItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                description = "This is a description",
                iconPainter = painterResource(id = R.drawable.ic_distance),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithLongDescAndIconItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                description = "This is a description with two lines",
                iconPainter = painterResource(id = R.drawable.ic_distance),
                actions = {

                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_remove_circle,
                            ),
                            contentDescription = null,
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_edit,
                            ),
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithLongDescTrailingAndIconItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                description = "This is a description with two lines",
                trailing = "100+",
                iconPainter = painterResource(id = R.drawable.ic_distance),
                actions = {

                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.ic_edit,
                            ),
                            contentDescription = null,
                        )
                    }
                },
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithIconItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                iconPainter = painterResource(id = R.drawable.ic_distance),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewCheckableListItemChecked() {
    LiftAppTheme {
        Surface {
            val (checked, setChecked) = remember { mutableStateOf(true) }
            CheckableListItem(
                title = "This is a title",
                description = "This is a description",
                iconPainter = painterResource(id = R.drawable.ic_distance),
                checked = checked,
                onCheckedChange = setChecked,
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewCheckableListItemUnchecked() {
    LiftAppTheme {
        Surface {
            val (checked, setChecked) = remember { mutableStateOf(false) }
            CheckableListItem(
                title = "This is a title",
                description = "This is a description",
                iconPainter = painterResource(id = R.drawable.ic_distance),
                checked = checked,
                onCheckedChange = setChecked,
            )
        }
    }
}
