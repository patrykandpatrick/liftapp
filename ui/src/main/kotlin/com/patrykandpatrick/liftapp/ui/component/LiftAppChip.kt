package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.patrykandpatrick.liftapp.ui.icons.CirclePlus
import com.patrykandpatrick.liftapp.ui.icons.Dropdown
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

@Composable
fun LiftAppChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    colors: ContainerColors = LiftAppCardDefaults.outlinedColors,
    contentPadding: PaddingValues = LiftAppFilterChipDefaults.contentPadding(),
    interactionSource: MutableInteractionSource? = null,
    label: @Composable () -> Unit,
) {
    LiftAppFilterChip(
        selected = false,
        onClick = onClick,
        enabled = enabled,
        label = label,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = StatefulContainerColors(colors, colors),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

@ComponentPreview
@Composable
fun LiftAppChipPreview() {
    LiftAppTheme {
        GridPreviewSurface(
            content =
                listOf(
                    "Chip" to { LiftAppChip(onClick = {}) { Text("Chip") } },
                    "Chip with leading icon" to
                        {
                            LiftAppChip(
                                onClick = {},
                                leadingIcon = { Icon(LiftAppIcons.CirclePlus, null) },
                            ) {
                                Text("Chip")
                            }
                        },
                    "Dropdown Chip" to
                        {
                            LiftAppChip(
                                onClick = {},
                                trailingIcon = { Icon(LiftAppIcons.Dropdown, null) },
                            ) {
                                Text("Chip")
                            }
                        },
                )
        )
    }
}
