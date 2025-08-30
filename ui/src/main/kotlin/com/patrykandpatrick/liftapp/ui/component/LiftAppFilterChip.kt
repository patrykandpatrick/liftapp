package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Check
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun LiftAppFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: StatefulContainerColors = LiftAppFilterChipDefaults.colors,
    contentPadding: PaddingValues = LiftAppFilterChipDefaults.contentPadding(),
    interactionSource: MutableInteractionSource? = null,
) {
    val containerColors = animateContainerColorsAsState(colors.getColors(selected)).value

    CardBase(
        enabled = enabled,
        colors = containerColors,
        interactionSource = interactionSource,
        modifier = modifier,
        textStyle = MaterialTheme.typography.labelMedium,
    ) { interactionSource ->
        val shape = CircleShape
        LookaheadScope {
            val spacing = dimens.chip.spacing
            val horizontalPadding = dimens.chip.horizontalPadding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier =
                    Modifier.animateBounds(this)
                        .interactiveButtonEffect(
                            colors = containerColors.interactiveBorderColors,
                            onClick = onClick,
                            enabled = enabled,
                            role = Role.Checkbox,
                            shape = shape,
                            checked = selected,
                        )
                        .background(
                            color =
                                if (enabled) containerColors.backgroundColor
                                else containerColors.disabledBackgroundColor,
                            shape = shape,
                        )
                        .padding(contentPadding)
                        .defaultMinSize(0.dp, dimens.chip.minHeight)
                        .align(Alignment.Center)
                        .fillMaxWidth(),
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                } else {
                    Spacer(Modifier.width(horizontalPadding - spacing))
                }

                label()

                if (trailingIcon != null) {
                    trailingIcon()
                } else {
                    Spacer(Modifier.width(horizontalPadding - spacing))
                }
            }
        }
    }
}

@Composable
fun LiftAppChipRow(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical =
        Arrangement.spacedBy(dimens.padding.itemVerticalMedium),
    horizontalArrangement: Arrangement.Horizontal =
        Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
    content: @Composable () -> Unit,
) {
    FlowRow(
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier,
    ) {
        LookaheadScope { content() }
    }
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
                                leadingIcon = { LiftAppFilterChipDefaults.Icon(LiftAppIcons.Check) },
                            )
                        },
                    "Chip with leading icon" to
                        {
                            FilterChipPreview(
                                selected = false,
                                leadingIcon = { LiftAppFilterChipDefaults.Icon(LiftAppIcons.Check) },
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
