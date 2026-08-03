package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonVisualEffect
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.Alpha
import com.patrykandpatrick.liftapp.ui.theme.LargeCornerRadius
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

data class LiftAppListItemPosition(val index: Int, val count: Int) {
    init {
        require(count > 0) { "A segmented list must contain at least one item." }
        require(index in 0 until count) { "The item index must be within the segmented list." }
    }

    companion object {
        val Single = LiftAppListItemPosition(index = 0, count = 1)
    }
}

@Composable
fun LiftAppListItem(
    title: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    position: LiftAppListItemPosition = LiftAppListItemPosition.Single,
    description: String? = null,
    trailing: String? = null,
    enabled: Boolean = true,
    checked: Boolean? = null,
    selected: Boolean? = null,
    nextItemSelected: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    titleHighlightPosition: IntRange = IntRange.EMPTY,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    shape: Shape? = null,
    onClick: (() -> Unit)? = null,
) {
    LiftAppListItem(
        title = {
            LiftAppListItemDefaults.Title(
                text = title,
                highlightPosition = titleHighlightPosition,
            )
        },
        modifier = modifier,
        position = position,
        description = description?.let { { Text(it) } },
        icon = {
            LiftAppListItemDefaults.Icon {
                Icon(imageVector = imageVector, contentDescription = null)
            }
        },
        trailing = trailing,
        actions = actions,
        enabled = enabled,
        checked = checked,
        selected = selected,
        nextItemSelected = nextItemSelected,
        onCheckedChange = onCheckedChange,
        interactionSource = interactionSource,
        role = role,
        shape = shape,
        onClick = onClick,
    )
}

@Composable
fun LiftAppListItem(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    position: LiftAppListItemPosition = LiftAppListItemPosition.Single,
    description: (@Composable () -> Unit)? = null,
    trailing: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    enabled: Boolean = true,
    checked: Boolean? = null,
    selected: Boolean? = null,
    nextItemSelected: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    contentPadding: PaddingValues = LiftAppListItemDefaults.contentPadding,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    shape: Shape? = null,
    onClick: (() -> Unit)? = null,
) {
    require(checked == null || selected == null) {
        "A list item cannot be both checked and selected."
    }
    val resolvedInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by resolvedInteractionSource.collectIsPressedAsState()
    val isDragged by resolvedInteractionSource.collectIsDraggedAsState()
    val isSelected = checked == true || selected == true
    val contentAlpha = if (enabled) 1f else Alpha.disabled
    val feedbackShape =
        shape
            ?: animatedFeedbackShape(
                position = position,
                expanded = isSelected || isPressed || isDragged,
            )
    val clickAction: (() -> Unit)? =
        if (checked != null && onCheckedChange != null) {
            { onCheckedChange(!checked) }
        } else {
            onClick
        }
    val resolvedRole =
        role
            ?: when {
                checked != null -> Role.Checkbox
                selected != null -> Role.RadioButton
                else -> null
            }
    val feedbackColors =
        InteractiveBorderColors(
            color = Color.Transparent,
            pressedColor = colorScheme.outline,
            hoverForegroundColor = colorScheme.primary,
            hoverBackgroundColor = colorScheme.outline,
            checkedColor = colorScheme.primary,
        )
    val gapAfter by
        animateDpAsState(
            targetValue =
                LiftAppListItemDefaults.gapAfter(
                    position = position,
                    selected = isSelected,
                    nextItemSelected = nextItemSelected,
                ),
            animationSpec = tween(durationMillis = ListItemFeedbackDurationMillis),
            label = "List item gap",
        )
    val positionedModifier = modifier.padding(bottom = gapAfter)
    val itemModifier =
        when {
            clickAction != null ->
                positionedModifier.interactiveButtonEffect(
                    colors = feedbackColors,
                    interactionSource = resolvedInteractionSource,
                    checked = isSelected,
                    shape = feedbackShape,
                    enabled = enabled,
                    onClick = clickAction,
                    role = resolvedRole,
                )
            interactionSource != null ->
                positionedModifier.interactiveButtonVisualEffect(
                    colors = feedbackColors,
                    interactionSource = resolvedInteractionSource,
                    checked = isSelected,
                    shape = feedbackShape,
                )
            else -> positionedModifier
        }
    val leadingContent = icon
    val trailingContent =
        if (trailing != null || actions != null) {
            @Composable {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (trailing != null) {
                        Text(
                            text = trailing,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.foregroundVariant,
                        )
                    }
                    CompositionLocalProvider(LocalContentColor provides colorScheme.foreground) {
                        actions?.invoke(this)
                    }
                }
            }
        } else {
            null
        }
    val itemContent =
        @Composable {
            CompositionLocalProvider(
                LocalContentColor provides colorScheme.foreground,
                LocalTextStyle provides MaterialTheme.typography.titleMedium,
            ) {
                title()
            }
        }
    val supportingContent = description?.let { content ->
        @Composable {
            CompositionLocalProvider(
                LocalContentColor provides colorScheme.foregroundVariant,
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
            ) {
                content()
            }
        }
    }

    val selectionProgress by
        animateFloatAsState(
            targetValue = if (isSelected) 1f else 0f,
            animationSpec = tween(durationMillis = ListItemFeedbackDurationMillis),
            label = "List item selection fill",
        )
    val selectedContainerColor = colorScheme.primaryDisabled.compositeOver(colorScheme.surface)
    val containerColor = lerp(colorScheme.surface, selectedContainerColor, selectionProgress)

    Row(
        modifier =
            itemModifier
                .alpha(contentAlpha)
                .semantics(mergeDescendants = true) {
                    if (!enabled) disabled()
                    if (checked != null) toggleableState = ToggleableState(checked)
                    if (selected != null) this.selected = selected
                }
                .fillMaxWidth()
                .background(containerColor, feedbackShape)
                .defaultMinSize(
                    minHeight =
                        if (description == null) {
                            LiftAppListItemDefaults.minHeight
                        } else {
                            LiftAppListItemDefaults.twoLineMinHeight
                        }
                )
                .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent != null) {
            Box(modifier = Modifier.padding(end = LiftAppListItemDefaults.internalSpacing)) {
                leadingContent()
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            itemContent()
            supportingContent?.invoke()
        }

        if (trailingContent != null) {
            Box(modifier = Modifier.padding(start = LiftAppListItemDefaults.internalSpacing)) {
                trailingContent()
            }
        }
    }
}

@Composable
private fun animatedFeedbackShape(
    position: LiftAppListItemPosition,
    expanded: Boolean,
): Shape {
    val target = LiftAppListItemDefaults.cornerRadii(position, expanded)
    val animationSpec = tween<Dp>(durationMillis = ListItemFeedbackDurationMillis)
    val topStart by
        animateDpAsState(
            targetValue = target.topStart,
            animationSpec = animationSpec,
            label = "List item top-start corner",
        )
    val topEnd by
        animateDpAsState(
            targetValue = target.topEnd,
            animationSpec = animationSpec,
            label = "List item top-end corner",
        )
    val bottomEnd by
        animateDpAsState(
            targetValue = target.bottomEnd,
            animationSpec = animationSpec,
            label = "List item bottom-end corner",
        )
    val bottomStart by
        animateDpAsState(
            targetValue = target.bottomStart,
            animationSpec = animationSpec,
            label = "List item bottom-start corner",
        )

    return RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart)
}

private const val ListItemFeedbackDurationMillis = 200

internal data class CornerRadii(
    val topStart: Dp,
    val topEnd: Dp,
    val bottomEnd: Dp,
    val bottomStart: Dp,
)

object LiftAppListItemDefaults {
    val iconSize = 40.dp
    val gap = 2.dp
    internal val minHeight = 56.dp
    internal val twoLineMinHeight = 72.dp
    internal val internalSpacing = 16.dp

    val contentPadding: PaddingValues
        @Composable get() = PaddingValues(horizontal = 16.dp, vertical = 10.dp)

    internal fun cornerRadii(position: LiftAppListItemPosition, expanded: Boolean): CornerRadii {
        val inner = 4.dp
        val outer = LargeCornerRadius
        if (expanded || position.count == 1) {
            return CornerRadii(outer, outer, outer, outer)
        }

        return when (position.index) {
            0 -> CornerRadii(outer, outer, inner, inner)
            position.count - 1 -> CornerRadii(inner, inner, outer, outer)
            else -> CornerRadii(inner, inner, inner, inner)
        }
    }

    internal fun gapAfter(
        position: LiftAppListItemPosition,
        selected: Boolean,
        nextItemSelected: Boolean,
    ): Dp =
        when {
            position.index == position.count - 1 -> 0.dp
            selected || nextItemSelected -> gap * 2
            else -> gap
        }

    val sectionHeadingStartPadding
        @Composable
        get() =
            LocalDimens.current.screen.padding +
                contentPadding.calculateStartPadding(LocalLayoutDirection.current)

    @Composable
    fun LeadingText(text: String) {
        val fixedFontScaleDensity = Density(LocalDensity.current.density)
        CompositionLocalProvider(LocalDensity provides fixedFontScaleDensity) {
            Icon {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }

    @Composable
    fun Icon(modifier: Modifier = Modifier, icon: @Composable BoxScope.() -> Unit) {
        Box(
            modifier = modifier.padding(vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            IconCircle(icon = icon)
        }
    }

    @Composable
    fun IconCircle(
        modifier: Modifier = Modifier,
        containerColor: Color = colorScheme.foregroundVariant,
        contentColor: Color = colorScheme.surface,
        icon: @Composable BoxScope.() -> Unit,
    ) {
        Box(
            modifier =
                modifier
                    .requiredSize(iconSize)
                    .background(color = containerColor, shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Box(modifier = Modifier.size(24.dp), content = icon)
            }
        }
    }

    @Composable
    fun Title(text: String, highlightPosition: IntRange = IntRange.EMPTY) {
        if (highlightPosition.isEmpty()) {
            TitleText(text)
            return
        }

        val highlightStart = highlightPosition.first.coerceIn(0, text.length)
        val highlightEnd = (highlightPosition.last + 1).coerceIn(highlightStart, text.length)
        var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
        val highlightColor = colorScheme.primary
        val highlightCornerRadiusPx = with(LocalDensity.current) { 4.dp.toPx() }
        TitleText(
            text = text,
            modifier =
                Modifier.drawBehind {
                    drawTitleHighlight(
                        textLayoutResult = textLayoutResult,
                        highlightPosition = highlightPosition,
                        color = highlightColor,
                        cornerRadius = highlightCornerRadiusPx,
                    )
                },
            spanStyles =
                listOf(
                    AnnotatedString.Range(
                        item = SpanStyle(colorScheme.onPrimary),
                        start = highlightStart,
                        end = highlightEnd,
                    )
                ),
            onTextLayout = { textLayoutResult = it },
        )
    }

    @Composable
    private fun TitleText(
        text: String,
        modifier: Modifier = Modifier,
        spanStyles: List<AnnotatedString.Range<SpanStyle>> = emptyList(),
        onTextLayout: (TextLayoutResult) -> Unit = {},
    ) {
        Text(
            text = AnnotatedString(text, spanStyles),
            modifier = modifier,
            color = colorScheme.foreground,
            onTextLayout = onTextLayout,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

private fun DrawScope.drawTitleHighlight(
    textLayoutResult: TextLayoutResult?,
    highlightPosition: IntRange,
    color: Color,
    cornerRadius: Float,
) {
    textLayoutResult ?: return
    highlightPosition
        .filter { it in 0 until textLayoutResult.layoutInput.text.length }
        .map(textLayoutResult::getBoundingBox)
        .groupBy { it.bottom }
        .forEach { (_, boundingBoxes) ->
            val boundingBox = boundingBoxes.reduce { combined, box ->
                Rect(
                    left = minOf(combined.left, box.left),
                    top = minOf(combined.top, box.top),
                    right = maxOf(combined.right, box.right),
                    bottom = maxOf(combined.bottom, box.bottom),
                )
            }
            drawRoundRect(
                color = color,
                topLeft = boundingBox.topLeft,
                size = boundingBox.size,
                cornerRadius = CornerRadius(cornerRadius),
            )
        }
}

@LightAndDarkThemePreview
@Composable
private fun LiftAppListItemPreview() {
    GridPreviewSurface(
        content =
            listOf(
                "Segmented list" to
                    {
                        Column(modifier = Modifier.width(320.dp)) {
                            repeat(3) { index ->
                                LiftAppListItem(
                                    title = { Text("Item ${index + 1}") },
                                    description = { Text("Description") },
                                    icon = { LiftAppListItemDefaults.LeadingText("${index + 1}") },
                                    position = LiftAppListItemPosition(index = index, count = 3),
                                    onClick = {},
                                )
                            }
                        }
                    },
                "States" to
                    {
                        Column(modifier = Modifier.width(320.dp)) {
                            LiftAppListItem(
                                title = { Text("Selected") },
                                position = LiftAppListItemPosition(index = 0, count = 3),
                                selected = true,
                                onClick = {},
                            )
                            LiftAppListItem(
                                title = { Text("Checked") },
                                position = LiftAppListItemPosition(index = 1, count = 3),
                                checked = true,
                                onCheckedChange = {},
                            )
                            LiftAppListItem(
                                title = { Text("Disabled") },
                                position = LiftAppListItemPosition(index = 2, count = 3),
                                enabled = false,
                                onClick = {},
                            )
                        }
                    },
            )
    )
}
