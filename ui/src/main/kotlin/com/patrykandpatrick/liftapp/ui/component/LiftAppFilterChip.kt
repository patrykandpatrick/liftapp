package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Check
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

@Composable
fun LiftAppFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    leadingIconVisible: Boolean = leadingIcon != null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: StatefulContainerColors = LiftAppFilterChipDefaults.colors,
    contentPadding: PaddingValues = LiftAppFilterChipDefaults.contentPadding(),
    interactionSource: MutableInteractionSource? = null,
) {
    val targetColors = colors.getColors(selected)
    val containerColors = animateContainerColorsAsState(targetColors).value

    CardBase(
        enabled = enabled,
        colors = containerColors,
        interactionSource = interactionSource,
        modifier = modifier,
        textStyle = MaterialTheme.typography.labelMedium,
    ) { interactionSource ->
        val shape = CircleShape
        val spacing = dimens.chip.spacing
        val horizontalPadding = dimens.chip.horizontalPadding
        val iconEdgeInset = 2.dp
        val leadingIconProgress by
            animateFloatAsState(
                targetValue = if (leadingIcon != null && leadingIconVisible) 1f else 0f,
                animationSpec = tween(durationMillis = 200),
                label = "Filter chip leading icon",
            )
        val expandedLeadingIconWidth = iconEdgeInset + dimens.chip.iconSize + spacing
        val leadingIconWidth =
            horizontalPadding + (expandedLeadingIconWidth - horizontalPadding) * leadingIconProgress

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.interactiveButtonEffect(
                        colors = targetColors.interactiveBorderColors,
                        onClick = onClick,
                        enabled = enabled,
                        role = Role.Checkbox,
                        shape = shape,
                        checked = selected,
                        interactionSource = interactionSource,
                    )
                    .clip(shape)
                    .background(
                        color =
                            if (enabled) {
                                containerColors.backgroundColor
                            } else {
                                containerColors.disabledBackgroundColor
                            },
                        shape = shape,
                    )
                    .padding(contentPadding)
                    .defaultMinSize(0.dp, dimens.chip.minHeight)
                    .align(Alignment.Center)
                    .fillMaxWidth(),
        ) {
            if (leadingIcon != null) {
                Box(modifier = Modifier.width(leadingIconWidth).height(dimens.chip.iconSize)) {
                    Box(
                        modifier =
                            Modifier.offset(
                                    x = leadingIconWidth - expandedLeadingIconWidth + iconEdgeInset
                                )
                                .graphicsLayer { alpha = leadingIconProgress }
                    ) {
                        leadingIcon()
                    }
                }
            } else {
                Spacer(Modifier.width(horizontalPadding))
            }

            label()

            if (trailingIcon != null) {
                Spacer(Modifier.width(spacing))
                trailingIcon()
                Spacer(Modifier.width(iconEdgeInset))
            } else {
                Spacer(Modifier.width(horizontalPadding))
            }
        }
    }
}

@Composable
fun LiftAppChipRow(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    content: @Composable () -> Unit,
) {
    FlowRow(
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier,
        content = { content() },
    )
}

object LiftAppFilterChipDefaults {
    val colors: StatefulContainerColors
        @Composable
        get() =
            StatefulContainerColors(
                colors = LiftAppCardDefaults.outlinedColors,
                checkedColors = LiftAppCardDefaults.tonalCardColors,
            )

    @Composable
    fun Icon(
        vector: ImageVector,
        modifier: Modifier = Modifier,
        contentDescription: String? = null,
    ) {
        Icon(
            imageVector = vector,
            contentDescription = contentDescription,
            modifier = modifier.size(dimens.chip.iconSize),
        )
    }

    @Composable
    fun contentPadding(): PaddingValues =
        PaddingValues(dimens.chip.horizontalPadding, dimens.chip.verticalPadding)
}

@ComponentPreview
@Composable
fun LiftAppFilterChipPreview() {
    LiftAppTheme {
        GridPreviewSurface(
            content =
                listOf(
                    "Selected Chip" to { FilterChipPreview(selected = true) },
                    "Unselected Chip" to { FilterChipPreview(selected = false) },
                    "Selected Chip with leading icon" to
                        {
                            FilterChipPreview(
                                selected = true,
                                leadingIcon = {
                                    LiftAppFilterChipDefaults.Icon(LiftAppIcons.Check)
                                },
                            )
                        },
                    "Chip with leading icon" to
                        {
                            FilterChipPreview(
                                selected = false,
                                leadingIcon = {
                                    LiftAppFilterChipDefaults.Icon(LiftAppIcons.Check)
                                },
                            )
                        },
                    "ChipRow" to
                        {
                            LiftAppChipRow {
                                FilterChipPreview(selected = true)
                                FilterChipPreview(selected = false)
                            }
                        },
                )
        )
    }
}

@Composable
private fun FilterChipPreview(
    selected: Boolean,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val (selected, setSelected) = remember { mutableStateOf(selected) }
    LiftAppFilterChip(
        selected = selected,
        onClick = { setSelected(!selected) },
        label = { Text(if (selected) "Selected" else "Unselected") },
        modifier = modifier,
        leadingIcon = leadingIcon,
    )
}
