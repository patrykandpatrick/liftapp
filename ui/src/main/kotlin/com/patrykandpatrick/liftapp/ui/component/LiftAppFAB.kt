package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Check
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = dimens.fab.iconPadding,
    enabled: Boolean = true,
    colors: ContainerColors = LiftAppFABDefaults.buttonColors,
    contentPadding: PaddingValues =
        PaddingValues(dimens.fab.horizontalPadding, dimens.fab.verticalPadding),
    margins: PaddingValues = PaddingValues(0.dp),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = CircleShape

    CardBase(
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        modifier = modifier,
        textStyle = MaterialTheme.typography.labelLarge,
    ) { interactionSource ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
            modifier =
                Modifier.padding(margins)
                    .interactiveButtonEffect(
                        colors = colors.interactiveBorderColors,
                        onClick = onClick,
                        enabled = enabled,
                        role = Role.Button,
                        shape = shape,
                    )
                    .dropShadow(shape) {
                        color = Color.Black.copy(alpha = .24f)
                        radius = 2.dp.toPx()
                        spread = 1.dp.toPx()
                        offset = Offset(0f, 1.dp.toPx())
                    }
                    .dropShadow(shape) {
                        color = colors.backgroundColor.copy(alpha = .12f)
                        radius = 8.dp.toPx()
                        spread = 2.dp.toPx()
                        offset = Offset(0f, 2.dp.toPx())
                    }
                    .dropShadow(shape) {
                        color = colors.backgroundColor.copy(alpha = .08f)
                        radius = 4.dp.toPx()
                        spread = 1.dp.toPx()
                    }
                    .background(color = colors.getBackgroundColor(enabled), shape = shape)
                    .innerShadow(shape) {
                        color = Color.White.copy(alpha = .5f)
                        spread = .5f.dp.toPx()
                        radius = .5f.dp.toPx()
                        offset = Offset(0f, 1.dp.toPx())
                        blendMode = BlendMode.Overlay
                    }
                    .innerShadow(shape) {
                        color = Color.Black.copy(alpha = .25f)
                        spread = .5f.dp.toPx()
                        radius = .5f.dp.toPx()
                        offset = Offset(0f, -1.dp.toPx())
                        blendMode = BlendMode.Overlay
                    }
                    .padding(contentPadding)
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .heightIn(min = dimens.button.minContentHeight),
            content = content,
        )
    }
}

object LiftAppFABDefaults {
    val buttonColors: ContainerColors
        @Composable
        get() =
            ContainerColors(
                backgroundColor = colorScheme.secondary,
                contentColor = colorScheme.onSecondary,
                interactiveBorderColors =
                    InteractiveBorderColors(
                        color = Color.Transparent,
                        pressedColor = colorScheme.primaryHighlightActivated,
                        hoverForegroundColor = colorScheme.primaryHighlightActivated,
                    ),
                disabledBackgroundColor = colorScheme.secondary,
                disabledContentColor = colorScheme.onSecondary,
            )
}

@ComponentPreview
@Composable
private fun LiftAppFABPreview() {
    GridPreviewSurface(
        content =
            listOf(
                "Button" to
                    {
                        LiftAppButton(onClick = {}) {
                            Icon(LiftAppIcons.Check, null)
                            Text(text = "Action")
                        }
                    },
                "FAB" to
                    {
                        LiftAppFAB(onClick = {}) {
                            Icon(LiftAppIcons.Check, null)
                            Text(text = "Action")
                        }
                    },
            )
    )
}
