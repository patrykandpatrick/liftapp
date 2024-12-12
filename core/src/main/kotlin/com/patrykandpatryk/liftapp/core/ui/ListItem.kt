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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults.ListItemTitle
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults.getDefaultDescription
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults.getDefaultIcon
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.Alpha
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape
import com.patrykandpatryk.liftapp.domain.extension.length

@Composable
fun ListItem(
    title: String,
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    enabled: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        title = { ListItemTitle(title, titleHighlightPosition) },
        modifier = modifier,
        description = getDefaultDescription(description),
        trailing = trailing,
        icon = getDefaultIcon(iconPainter),
        actions = actions,
        enabled = enabled,
        paddingValues = paddingValues,
        onClick = onClick,
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
    onCheckedChange: (Boolean) -> Unit,
) {
    val animationFraction by
        animateFloatAsState(targetValue = if (checked) 1f else 0f, animationSpec = tween())

    val shape = MaterialTheme.shapes.small.scaleCornerSize(animationFraction)

    ListItem(
        title = { Text(title) },
        modifier =
            modifier
                .border(
                    width = MaterialTheme.dimens.strokeWidth,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = animationFraction),
                    shape = shape,
                )
                .clip(shape),
        description = getDefaultDescription(description),
        trailing = trailing,
        icon = {
            if (iconPainter != null) {
                Icon(
                    modifier =
                        Modifier.size(MaterialTheme.dimens.list.itemIconBackgroundSize)
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
            Checkbox(checked = checked, onCheckedChange = if (enabled) onCheckedChange else null)
        },
        enabled = enabled,
        paddingValues = paddingValues,
        onClick = { onCheckedChange(checked.not()) },
    )
}

@Composable
fun ListItem(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: @Composable (() -> Unit)? = null,
    trailing: String? = null,
    icon: @Composable (RowScope.() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit) = {},
    enabled: Boolean = true,
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontal),
        modifier =
            modifier
                .alpha(Alpha.get(enabled))
                .fillMaxWidth()
                .thenIfNotNull(value = onClick) { clickable(onClick = it, enabled = enabled) }
                .padding(paddingValues),
    ) {
        icon?.invoke(this)

        Column(modifier = Modifier.weight(1f)) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleMedium,
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                content = title,
            )

            if (description != null) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                    content = description,
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

        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

object ListItemDefaults {
    val paddingValues: PaddingValues
        @Composable
        get() =
            PaddingValues(
                start = LocalDimens.current.padding.contentHorizontal,
                top = LocalDimens.current.padding.itemVertical,
                end = LocalDimens.current.padding.contentHorizontalSmall,
                bottom = LocalDimens.current.padding.itemVertical,
            )

    internal fun getDefaultIcon(painter: Painter?): (@Composable RowScope.() -> Unit)? =
        if (painter != null) {
            {
                Icon(
                    modifier =
                        Modifier.size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = PillShape,
                            )
                            .padding(8.dp),
                    painter = painter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        } else null

    @Composable
    fun ListItemTitle(title: String, titleHighlightPosition: IntRange) {
        if (!titleHighlightPosition.isEmpty()) {
            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            val highlightColor = MaterialTheme.colorScheme.tertiaryContainer
            val highlightCornerRadiusPx =
                with(LocalDensity.current) {
                    MaterialTheme.dimens.list.itemTitleHighlightCornerRadius.toPx()
                }
            ListItemTitle(
                text = title,
                modifier =
                    Modifier.drawBehind {
                        textLayoutResult?.run {
                            titleHighlightPosition
                                .take(titleHighlightPosition.length)
                                .map { getBoundingBox(it) }
                                .groupBy { it.bottom }
                                .forEach { (_, boundingBoxes) ->
                                    val boundingBox =
                                        boundingBoxes
                                            .first()
                                            .copy(right = boundingBoxes.last().right)
                                    drawRoundRect(
                                        highlightColor,
                                        boundingBox.topLeft,
                                        boundingBox.size,
                                        CornerRadius(highlightCornerRadiusPx),
                                    )
                                }
                        }
                    },
                spanStyles =
                    listOf(
                        AnnotatedString.Range(
                            SpanStyle(MaterialTheme.colorScheme.onTertiaryContainer),
                            titleHighlightPosition.first,
                            titleHighlightPosition.last,
                        )
                    ),
                onTextLayout = { textLayoutResult = it },
            )
        } else {
            ListItemTitle(title)
        }
    }

    @Composable
    fun ListItemTitle(
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

    internal fun getDefaultDescription(description: String?): (@Composable () -> Unit)? {
        return if (description != null) {
            { Text(description) }
        } else null
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleItem() {
    LiftAppTheme { Surface { ListItem(title = { Text("This is a title") }) } }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithDescItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = { Text("This is a title") },
                description = { Text("This is a description") },
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
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove_circle),
                            contentDescription = null,
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
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
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
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
