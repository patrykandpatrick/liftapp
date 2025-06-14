package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppCard(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ContainerColors = LiftAppCardDefaults.cardColors,
    contentPadding: PaddingValues =
        PaddingValues(dimens.padding.itemHorizontal, dimens.padding.itemVertical),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    shape: Shape = MaterialTheme.shapes.medium,
    interactionSource: MutableInteractionSource? = null,
    role: Role? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    CardBase(
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        modifier = modifier,
    ) { interactionSource ->
        Column(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            modifier =
                Modifier.interactiveButtonEffect(
                        colors = colors.interactiveBorderColors,
                        onClick = onClick,
                        enabled = enabled,
                        role = role,
                        shape = shape,
                    )
                    .clip(shape)
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
internal fun CardBase(
    enabled: Boolean,
    colors: ContainerColors,
    interactionSource: MutableInteractionSource?,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    content: @Composable BoxScope.(MutableInteractionSource) -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    CompositionLocalProvider(
        LocalContentColor provides
            if (enabled) colors.contentColor else colors.disabledContentColor,
        LocalTextStyle provides LocalTextStyle.current.merge(textStyle),
    ) {
        Box(modifier = modifier.width(IntrinsicSize.Max).height(IntrinsicSize.Max)) {
            content(interactionSource)
        }
    }
}

object LiftAppCardDefaults {
    val cardColors: ContainerColors
        @Composable
        get() =
            ContainerColors(
                backgroundColor = colorScheme.surface,
                contentColor = colorScheme.onSurface,
                interactiveBorderColors =
                    InteractiveBorderColors(
                        color = colorScheme.outline,
                        pressedColor = colorScheme.primary,
                        hoverForegroundColor = colorScheme.primary,
                    ),
                disabledBackgroundColor = Color.Transparent,
                disabledContentColor = colorScheme.secondaryDisabled,
            )

    val tonalCardColors: ContainerColors
        @Composable
        get() =
            ContainerColors(
                backgroundColor = colorScheme.primaryDisabled,
                contentColor = colorScheme.onSurface,
                interactiveBorderColors =
                    InteractiveBorderColors(
                        color = colorScheme.primary,
                        pressedColor = colorScheme.primaryHighlightActivated,
                        hoverForegroundColor = colorScheme.primaryHighlightActivated,
                    ),
                disabledBackgroundColor = Color.Transparent,
                disabledContentColor = colorScheme.secondaryDisabled,
            )

    val outlinedColors: ContainerColors
        @Composable
        get() =
            ContainerColors(
                backgroundColor = Color.Transparent,
                contentColor = colorScheme.onSurface,
                interactiveBorderColors =
                    InteractiveBorderColors(
                        color = colorScheme.outline,
                        pressedColor = colorScheme.primary,
                        hoverForegroundColor = colorScheme.primary,
                    ),
                disabledBackgroundColor = Color.Transparent,
                disabledContentColor = colorScheme.secondaryDisabled,
            )

    val deselectedColors: ContainerColors
        @Composable
        get() =
            cardColors.run {
                copy(
                    backgroundColor = Color.Transparent,
                    interactiveBorderColors =
                        interactiveBorderColors.copy(color = Color.Transparent),
                )
            }
}

@Preview
@Composable
private fun CardPreview() {
    GridPreviewSurface(
        content =
            listOf(
                "Card" to
                    {
                        val (selected, setSelected) = remember { mutableStateOf(false) }
                        LiftAppCard(
                            onClick = { setSelected(!selected) },
                            enabled = true,
                            colors =
                                animateContainerColorsAsState(
                                        if (selected) LiftAppCardDefaults.tonalCardColors
                                        else LiftAppCardDefaults.cardColors
                                    )
                                    .value,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(
                                    imageVector = Icons.TwoTone.Face,
                                    null,
                                    Modifier.padding(bottom = 10.dp),
                                )

                                Text(
                                    text = "This is a card",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    text = "This is a description",
                                    style = MaterialTheme.typography.bodyMedium,
                                )

                                PlainLiftAppButton(
                                    onClick = {},
                                    modifier = Modifier.align(Alignment.End).padding(top = 12.dp),
                                ) {
                                    Text("Go")
                                }
                            }
                        }
                    },
                "Tonal Card" to
                    {
                        LiftAppCard(
                            onClick = {},
                            enabled = true,
                            colors = LiftAppCardDefaults.tonalCardColors,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(
                                    imageVector = Icons.TwoTone.Face,
                                    null,
                                    Modifier.padding(bottom = 10.dp),
                                )

                                Text(
                                    text = "This is a card",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    text = "This is a description",
                                    style = MaterialTheme.typography.bodyMedium,
                                )

                                PlainLiftAppButton(
                                    onClick = {},
                                    modifier = Modifier.align(Alignment.End).padding(top = 12.dp),
                                ) {
                                    Text("Go")
                                }
                            }
                        }
                    },
            )
    )
}
