package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.component.ContainerColors
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppCheckbox
import com.patrykandpatrick.liftapp.ui.component.StatefulContainerColors
import com.patrykandpatrick.liftapp.ui.component.animateContainerColorsAsState
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.Alpha
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults.ListItemTitle
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults.getDefaultDescription
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults.getDefaultIcon
import com.patrykandpatryk.liftapp.domain.extension.length

@Composable
fun ListItem(
    title: String,
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    enabled: Boolean = true,
    checked: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},
    colors: StatefulContainerColors = ListItemDefaults.colors,
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    interactionSource: MutableInteractionSource? = null,
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
        checked = checked,
        colors = colors,
        paddingValues = paddingValues,
        interactionSource = interactionSource,
        onClick = onClick,
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
    colors: StatefulContainerColors = ListItemDefaults.colors,
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    checked: Boolean = false,
    shape: Shape = MaterialTheme.shapes.medium,
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null,
) {
    val currentColors = animateContainerColorsAsState(colors.getColors(checked)).value

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontal),
        modifier =
            modifier
                .alpha(Alpha.get(enabled))
                .fillMaxWidth()
                .interactiveButtonEffect(
                    colors = currentColors.interactiveBorderColors,
                    onClick = onClick,
                    enabled = enabled,
                    checked = checked,
                    shape = shape,
                    interactionSource = interactionSource,
                )
                .background(color = currentColors.getBackgroundColor(enabled), shape = shape)
                .padding(paddingValues),
    ) {
        icon?.invoke(this)

        Column(modifier = Modifier.weight(1f)) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleMedium,
                LocalContentColor provides colorScheme.onSurface,
                content = title,
            )

            if (description != null) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                    LocalContentColor provides colorScheme.onSurfaceVariant,
                    content = description,
                )
            }
        }

        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

object ListItemDefaults {
    val colors: StatefulContainerColors
        @Composable
        get() =
            StatefulContainerColors(
                colors =
                    ContainerColors(
                        backgroundColor = Color.Transparent,
                        contentColor = colorScheme.onSurface,
                        interactiveBorderColors =
                            InteractiveBorderColors(
                                color = Color.Transparent,
                                pressedColor = colorScheme.outline,
                                hoverForegroundColor = colorScheme.primary,
                                hoverBackgroundColor = colorScheme.outline,
                            ),
                        disabledBackgroundColor = Color.Transparent,
                        disabledContentColor = colorScheme.onPrimaryDisabled,
                    ),
                checkedColors = LiftAppCardDefaults.tonalCardColors,
            )

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
                            .background(color = colorScheme.onSurfaceVariant, shape = PillShape)
                            .padding(8.dp),
                    painter = painter,
                    contentDescription = null,
                    tint = colorScheme.surface,
                )
            }
        } else null

    @Composable
    fun LeadingText(text: String) {
        Box(
            modifier =
                Modifier.size(40.dp)
                    .background(color = colorScheme.onSurfaceVariant, shape = PillShape)
                    .padding(8.dp)
        ) {
            val modifiedDensity = Density(LocalDensity.current.density)
            CompositionLocalProvider(LocalDensity provides modifiedDensity) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.surface,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }

    @Composable
    fun ListItemTitle(title: String, titleHighlightPosition: IntRange) {
        if (!titleHighlightPosition.isEmpty()) {
            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            val highlightColor = colorScheme.primary
            val highlightCornerRadiusPx =
                with(LocalDensity.current) { dimens.list.itemTitleHighlightCornerRadius.toPx() }
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
                            SpanStyle(colorScheme.onPrimary),
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
            color = colorScheme.onSurface,
            onTextLayout = onTextLayout,
            style = MaterialTheme.typography.titleMedium,
        )
    }

    internal fun getDefaultDescription(description: String?): (@Composable () -> Unit)? {
        return if (description != null) {
            { Text(description) }
        } else null
    }

    @Composable
    fun Checkbox(checked: Boolean, modifier: Modifier = Modifier) {
        LiftAppCheckbox(
            checked = checked,
            onCheckedChange = null,
            modifier = modifier.padding(horizontal = dimens.padding.itemHorizontalSmall),
        )
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleItem() {
    LiftAppTheme { LiftAppBackground { ListItem(title = { Text("This is a title") }) } }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithDescItem() {
    LiftAppTheme {
        LiftAppBackground {
            ListItem(
                title = { ListItemTitle("This is a title", titleHighlightPosition = 0..3) },
                description = { Text("This is a description") },
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewTitleWithDescAndIconItem() {
    LiftAppTheme {
        LiftAppBackground {
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
        LiftAppBackground {
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
        LiftAppBackground {
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
        LiftAppBackground {
            ListItem(
                title = "This is a title",
                iconPainter = painterResource(id = R.drawable.ic_distance),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun PreviewCheckableListItemChecked() {
    PreviewCheckableListItem(checked = true)
}

@LightAndDarkThemePreview
@Composable
private fun PreviewCheckableListItemUnchecked() {
    PreviewCheckableListItem(checked = false)
}

@Composable
private fun PreviewCheckableListItem(checked: Boolean) {
    LiftAppTheme {
        LiftAppBackground {
            val (checked, setChecked) = remember { mutableStateOf(checked) }
            ListItem(
                title = "This is a title",
                titleHighlightPosition = 0..3,
                description = "This is a description",
                iconPainter = painterResource(id = R.drawable.ic_distance),
                checked = checked,
                onClick = { setChecked(!checked) },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
