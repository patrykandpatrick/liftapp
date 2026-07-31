package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.extension.calculateStartPadding
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.ui.ListItemDefaults.ListItemTitle
import com.patrykandpatrick.liftapp.core.ui.ListItemDefaults.getDefaultDescription
import com.patrykandpatrick.liftapp.core.ui.ListItemDefaults.getDefaultIcon
import com.patrykandpatrick.liftapp.domain.extension.length
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.component.ContainerColors
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppCheckbox
import com.patrykandpatrick.liftapp.ui.component.StatefulContainerColors
import com.patrykandpatrick.liftapp.ui.component.animateContainerColorsAsState
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.CircleMinus
import com.patrykandpatrick.liftapp.ui.icons.Edit
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Ruler
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.Alpha
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun ListItem(
    title: String,
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    enabled: Boolean = true,
    checked: Boolean? = null,
    actions: @Composable RowScope.() -> Unit = {},
    colors: StatefulContainerColors = ListItemDefaults.colors,
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    horizontalVisualInset: Dp = ListItemDefaults.horizontalVisualInset(checked),
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
        horizontalVisualInset = horizontalVisualInset,
        interactionSource = interactionSource,
        onClick = onClick,
    )
}

@Composable
fun ListItem(
    title: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    enabled: Boolean = true,
    checked: Boolean? = null,
    actions: @Composable RowScope.() -> Unit = {},
    colors: StatefulContainerColors = ListItemDefaults.colors,
    paddingValues: PaddingValues = ListItemDefaults.paddingValues,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    horizontalVisualInset: Dp = ListItemDefaults.horizontalVisualInset(checked),
    interactionSource: MutableInteractionSource? = null,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        title = { ListItemTitle(title, titleHighlightPosition) },
        modifier = modifier,
        description = getDefaultDescription(description),
        trailing = trailing,
        icon = getDefaultIcon(imageVector),
        actions = actions,
        enabled = enabled,
        checked = checked,
        colors = colors,
        paddingValues = paddingValues,
        horizontalVisualInset = horizontalVisualInset,
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
    checked: Boolean? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    /**
     * How far the background and border are drawn inside the row, and how far the content is pushed
     * in to match. A checkable row takes it so its fill does not run to the screen edge; pass the
     * same value to a plain row sitting under one, or their text will not line up.
     */
    horizontalVisualInset: Dp = ListItemDefaults.horizontalVisualInset(checked),
    interactionSource: MutableInteractionSource? = null,
    horizontalSpacing: Dp = ListItemDefaults.horizontalSpacing,
    onClick: (() -> Unit)? = null,
) {
    val isChecked = checked == true
    val contentPadding = paddingValues.increaseBy(start = horizontalVisualInset)
    val targetColors = colors.getColors(isChecked)
    val currentColors = animateContainerColorsAsState(targetColors).value
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isDragged by resolvedInteractionSource.collectIsDraggedAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        modifier =
            modifier
                .alpha(Alpha.get(enabled))
                .fillMaxWidth()
                .interactiveButtonEffect(
                    // The background and border each animate from the same state change. Passing
                    // the already-animated border colors here would animate them a second time,
                    // leaving the border visibly behind the fill.
                    colors = targetColors.interactiveBorderColors,
                    onClick = onClick,
                    enabled = enabled,
                    borderHorizontalInset = horizontalVisualInset,
                    checked = isChecked,
                    shape = shape,
                    interactionSource = resolvedInteractionSource,
                )
                .listItemBackground(
                    color =
                        if (isDragged) {
                            colorScheme.background
                        } else {
                            currentColors.getBackgroundColor(enabled)
                        },
                    shape = shape,
                    horizontalInset = horizontalVisualInset,
                )
                .padding(contentPadding),
    ) {
        icon?.invoke(this)

        ListItemText(
            title = title,
            description = description,
            modifier = Modifier.weight(1f),
        )

        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            CompositionLocalProvider(LocalContentColor provides colorScheme.onSurface) { actions() }
        }
    }
}

@Composable
fun ListItemText(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
    ) {
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
}

private fun Modifier.listItemBackground(
    color: Color,
    shape: Shape,
    horizontalInset: Dp,
): Modifier = drawWithCache {
    val horizontalInsetPx = horizontalInset.toPx()
    val outline =
        shape.createOutline(
            size =
                Size(
                    width = (size.width - horizontalInsetPx * 2).coerceAtLeast(0f),
                    height = size.height,
                ),
            layoutDirection = layoutDirection,
            density = this,
        )

    onDrawBehind {
        translate(left = horizontalInsetPx) { drawOutline(outline = outline, color = color) }
    }
}

object ListItemDefaults {
    val iconSize = 40.dp
    val horizontalSpacing = 16.dp

    /** What a checkable row insets its background, border, and content by. */
    val horizontalVisualInset = 4.dp

    /** The inset a row takes when nothing asks for another: only a checkable row has one. */
    fun horizontalVisualInset(checked: Boolean?): Dp =
        if (checked != null) horizontalVisualInset else 0.dp

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
                start = LocalDimens.current.screen.horizontalPadding,
                top = 16.dp,
                end = LocalDimens.current.screen.horizontalPadding - 8.dp,
                bottom = 16.dp,
            )

    val leadingContentStartPadding: Dp
        @Composable get() = paddingValues.calculateStartPadding() + iconSize + horizontalSpacing

    internal fun getDefaultIcon(painter: Painter?): (@Composable RowScope.() -> Unit)? =
        if (painter != null) {
            {
                Icon(
                    modifier =
                        Modifier.size(iconSize)
                            .background(color = colorScheme.onSurfaceVariant, shape = PillShape)
                            .padding(8.dp),
                    painter = painter,
                    contentDescription = null,
                    tint = colorScheme.surface,
                )
            }
        } else null

    internal fun getDefaultIcon(imageVector: ImageVector?): (@Composable RowScope.() -> Unit)? =
        if (imageVector != null) {
            {
                Icon(
                    modifier =
                        Modifier.size(iconSize)
                            .background(color = colorScheme.onSurfaceVariant, shape = PillShape)
                            .padding(8.dp),
                    imageVector = imageVector,
                    contentDescription = null,
                    tint = colorScheme.surface,
                )
            }
        } else null

    @Composable
    fun LeadingText(text: String) {
        val modifiedDensity = Density(LocalDensity.current.density)
        CompositionLocalProvider(LocalDensity provides modifiedDensity) {
            Icon {
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
    fun Icon(modifier: Modifier = Modifier, icon: @Composable BoxScope.() -> Unit) {
        Box(
            modifier =
                modifier
                    .size(iconSize)
                    .background(color = colorScheme.onSurfaceVariant, shape = PillShape)
                    .padding(8.dp)
        ) {
            CompositionLocalProvider(LocalContentColor provides colorScheme.surface) { icon() }
        }
    }

    @Composable
    fun ListItemTitle(title: String, titleHighlightPosition: IntRange) {
        if (!titleHighlightPosition.isEmpty()) {
            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            val highlightColor = colorScheme.primary
            val highlightCornerRadiusPx = with(LocalDensity.current) { 4.dp.toPx() }
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
            modifier = modifier.padding(start = 8.dp, end = 12.dp),
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
                imageVector = LiftAppIcons.Ruler,
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
                imageVector = LiftAppIcons.Ruler,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = LiftAppIcons.CircleMinus, contentDescription = null)
                    }

                    IconButton(onClick = {}) {
                        Icon(imageVector = LiftAppIcons.Edit, contentDescription = null)
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
                imageVector = LiftAppIcons.Ruler,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = LiftAppIcons.Edit, contentDescription = null)
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
        LiftAppBackground { ListItem(title = "This is a title", imageVector = LiftAppIcons.Ruler) }
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
                imageVector = LiftAppIcons.Ruler,
                checked = checked,
                onClick = { setChecked(!checked) },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
