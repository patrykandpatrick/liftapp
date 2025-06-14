package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

object LiftAppButtonDefaults {
    val primaryButtonColors: ContainerColors
        @Composable
        get() =
            ContainerColors(
                backgroundColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                interactiveBorderColors =
                    InteractiveBorderColors(
                        color = colorScheme.primaryHighlight,
                        pressedColor = colorScheme.primaryHighlightActivated,
                        hoverForegroundColor = colorScheme.primaryHighlightActivated,
                    ),
                disabledBackgroundColor = colorScheme.primaryDisabled,
                disabledContentColor = colorScheme.secondaryDisabled,
            )

    val outlinedButtonColors: ContainerColors
        @Composable
        get() =
            ContainerColors(
                backgroundColor = colorScheme.surface,
                contentColor =
                    if (colorScheme.isDarkColorScheme) {
                        colorScheme.onPrimary
                    } else {
                        colorScheme.primary
                    },
                interactiveBorderColors =
                    InteractiveBorderColors(
                        color = colorScheme.outline,
                        pressedColor = colorScheme.primary,
                        hoverForegroundColor = colorScheme.primary,
                    ),
                disabledBackgroundColor = Color.Transparent,
                disabledContentColor = colorScheme.secondaryDisabled,
            )

    val plainButtonColors: ContainerColors
        @Composable
        get() =
            ContainerColors(
                backgroundColor = Color.Transparent,
                contentColor = colorScheme.onSurface,
                interactiveBorderColors =
                    InteractiveBorderColors(
                        color = Color.Transparent,
                        pressedColor = colorScheme.primary,
                        hoverForegroundColor = colorScheme.primary,
                        hoverBackgroundColor = colorScheme.outline,
                    ),
                disabledBackgroundColor = Color.Transparent,
                disabledContentColor = colorScheme.secondaryDisabled,
            )
}

@Composable
fun LiftAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = dimens.button.iconPadding,
    enabled: Boolean = true,
    colors: ContainerColors = LiftAppButtonDefaults.primaryButtonColors,
    contentPadding: PaddingValues =
        PaddingValues(dimens.button.horizontalPadding, dimens.button.verticalPadding),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    CardBase(
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        modifier = modifier,
        textStyle = MaterialTheme.typography.labelLarge,
    ) { interactionSource ->
        val shape = MaterialTheme.shapes.medium
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            modifier =
                Modifier.interactiveButtonEffect(
                        colors = colors.interactiveBorderColors,
                        onClick = onClick,
                        enabled = enabled,
                        role = Role.Button,
                        shape = shape,
                    )
                    .background(color = colors.getBackgroundColor(enabled), shape = shape)
                    .padding(contentPadding)
                    .align(Alignment.Center)
                    .fillMaxSize()
                    .heightIn(min = dimens.button.minContentHeight),
            content = content,
        )
    }
}

@Composable
fun PlainLiftAppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = 6.dp,
    enabled: Boolean = true,
    colors: ContainerColors = LiftAppButtonDefaults.plainButtonColors,
    contentPadding: PaddingValues =
        PaddingValues(
            horizontal = dimens.button.horizontalPadding,
            vertical = dimens.button.verticalPadding - 2.dp,
        ),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    CardBase(
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        modifier = modifier,
        textStyle = MaterialTheme.typography.labelLarge,
    ) { interactionSource ->
        val shape = MaterialTheme.shapes.small

        Layout(
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =
                        Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                    content = content,
                )
                if (enabled) {
                    SinHorizontalDivider(
                        color = LocalContentColor.current,
                        sinHeight = 4.dp,
                        thickness = dimens.button.underlineWidth,
                        sinPeriodLength = 1.5.dp,
                    )
                }
            },
            modifier =
                Modifier.interactiveButtonEffect(
                        colors = colors.interactiveBorderColors,
                        onClick = onClick,
                        enabled = enabled,
                        role = Role.Button,
                        shape = shape,
                    )
                    .padding(contentPadding)
                    .fillMaxWidth()
                    .heightIn(min = dimens.button.minContentHeight),
            measurePolicy =
                object : MeasurePolicy {
                    override fun MeasureScope.measure(
                        measurables: List<Measurable>,
                        constraints: Constraints,
                    ): MeasureResult {
                        val buttonContent = measurables[0].measure(constraints.copy(minHeight = 0))
                        val divider =
                            measurables
                                .getOrNull(1)
                                ?.measure(
                                    constraints.copy(
                                        minWidth = buttonContent.width,
                                        maxWidth = buttonContent.width,
                                        minHeight = 0,
                                        maxHeight =
                                            if (constraints.hasBoundedHeight) {
                                                constraints.maxHeight - buttonContent.height
                                            } else {
                                                constraints.maxHeight
                                            },
                                    )
                                )
                        val spacing = 2.dp.roundToPx()
                        return layout(
                            buttonContent.width,
                            buttonContent.height + spacing + (divider?.height ?: 0),
                        ) {
                            buttonContent.placeRelative(0, 0)
                            divider?.placeRelative(0, buttonContent.height + spacing)
                        }
                    }
                },
        )
    }
}

@ComponentPreview
@Composable
private fun PrimaryButtonPreview() {
    GridPreviewSurface(
        content =
            listOf(
                "Button" to { LiftAppButton(onClick = {}) { Text("LiftApp Button") } },
                "Button With Icon" to
                    {
                        LiftAppButton(onClick = {}) {
                            Icon(Icons.TwoTone.Face, contentDescription = null)
                            Text("LiftApp Button")
                        }
                    },
                "Button With Icon Disabled" to
                    {
                        LiftAppButton(onClick = {}, enabled = false) {
                            Icon(Icons.TwoTone.Face, contentDescription = null)
                            Text("LiftApp Button")
                        }
                    },
            )
    )
}

@ComponentPreview
@Composable
private fun OutlinedButtonPreview() {
    GridPreviewSurface(
        content =
            listOf(
                "Outlined Button" to
                    {
                        LiftAppButton(
                            onClick = {},
                            colors = LiftAppButtonDefaults.outlinedButtonColors,
                        ) {
                            Text(text = "LiftApp Button")
                        }
                    },
                "Outlined Button With Icon" to
                    {
                        LiftAppButton(
                            onClick = {},
                            colors = LiftAppButtonDefaults.outlinedButtonColors,
                        ) {
                            Icon(Icons.TwoTone.Face, contentDescription = null)
                            Text("LiftApp Button")
                        }
                    },
                "Outlined Button With Icon Disabled" to
                    {
                        LiftAppButton(
                            onClick = {},
                            colors = LiftAppButtonDefaults.outlinedButtonColors,
                            enabled = false,
                        ) {
                            Icon(Icons.TwoTone.Face, contentDescription = null)
                            Text("LiftApp Button")
                        }
                    },
            )
    )
}

@ComponentPreview
@Composable
private fun ButtonPreview() {
    GridPreviewSurface(
        content =
            listOf(
                "Plain Button" to { PlainLiftAppButton(onClick = {}) { Text("LiftApp Button") } },
                "Plain Button With Icon" to
                    {
                        PlainLiftAppButton(onClick = {}) {
                            Icon(Icons.TwoTone.Face, contentDescription = null)
                            Text("LiftApp Button")
                        }
                    },
                "Plain Button With Icon Only" to
                    {
                        PlainLiftAppButton(onClick = {}) {
                            Icon(Icons.TwoTone.Face, contentDescription = null)
                        }
                    },
                "Plain Button With Icon Disabled" to
                    {
                        PlainLiftAppButton(onClick = {}, enabled = false) {
                            Icon(Icons.TwoTone.Face, contentDescription = null)
                            Text("LiftApp Button")
                        }
                    },
            )
    )
}
