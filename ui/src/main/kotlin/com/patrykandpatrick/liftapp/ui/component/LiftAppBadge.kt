package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TrendingDown
import com.patrykandpatrick.liftapp.ui.icons.TrendingUp
import com.patrykandpatrick.liftapp.ui.preview.ComponentPreview
import com.patrykandpatrick.liftapp.ui.preview.GridPreviewSurface
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * A short status set in a pill, stated rather than offered: unlike [LiftAppChip] there is nothing
 * to tap, so it carries no touch target and can sit inline with the text it qualifies.
 *
 * [color] is the status's own — the app's green for a rise, red for a fall, and so on — and is what
 * the label, the icon, the fill, and the edge are all drawn in, so the badge reads as its status
 * before the label is. Pair [color] with an [icon] or a signed [label] wherever the status matters:
 * color alone excludes anyone who cannot separate the two hues.
 */
@Composable
fun LiftAppBadge(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentPadding: PaddingValues = LiftAppBadgeDefaults.contentPadding,
    iconSize: Dp = LiftAppBadgeDefaults.iconSize,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.badge.spacing),
        // The border is what gives the badge its shape, which lets the fill stay faint — a fill
        // tinted with the label's own hue costs contrast as it deepens.
        modifier =
            modifier
                .background(
                    color = color.copy(alpha = LiftAppBadgeDefaults.fillAlpha),
                    shape = PillShape,
                )
                .border(
                    width = dimens.badge.borderWidth,
                    color = color.copy(alpha = LiftAppBadgeDefaults.borderAlpha),
                    shape = PillShape,
                )
                .padding(contentPadding),
    ) {
        icon?.let { vector ->
            Icon(
                imageVector = vector,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(iconSize),
            )
        }

        LiftAppText(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1,
        )
    }
}

object LiftAppBadgeDefaults {
    /**
     * How strongly the badge's color tints the surface behind it. Kept faint because a fill in the
     * label's own hue moves toward the label as it deepens, costing the contrast it looks like it
     * should add.
     */
    const val fillAlpha = .12f

    /** How strongly the same color draws the edge, which carries the badge rather than the fill. */
    const val borderAlpha = .48f

    val contentPadding: PaddingValues
        @Composable
        get() = PaddingValues(dimens.badge.horizontalPadding, dimens.badge.verticalPadding)

    val iconSize: Dp
        @Composable get() = dimens.badge.iconSize

    /** For a badge sharing a line with text that needs the rest of the room. */
    val compactContentPadding: PaddingValues
        @Composable
        get() =
            PaddingValues(
                dimens.badge.compactHorizontalPadding,
                dimens.badge.compactVerticalPadding,
            )

    val compactIconSize: Dp
        @Composable get() = dimens.badge.compactIconSize
}

@ComponentPreview
@Composable
private fun LiftAppBadgePreview() {
    LiftAppTheme {
        GridPreviewSurface(
            content =
                listOf(
                    "Rise" to
                        {
                            LiftAppBadge(
                                label = "+1.2",
                                color = colorScheme.green,
                                icon = LiftAppIcons.TrendingUp,
                            )
                        },
                    "Fall" to
                        {
                            LiftAppBadge(
                                label = "-0.8",
                                color = colorScheme.red,
                                icon = LiftAppIcons.TrendingDown,
                            )
                        },
                    "No icon" to
                        {
                            LiftAppBadge(label = "0", color = colorScheme.onSurfaceVariant)
                        },
                    "Compact" to
                        {
                            LiftAppBadge(
                                label = "+1.2",
                                color = colorScheme.green,
                                icon = LiftAppIcons.TrendingUp,
                                contentPadding = LiftAppBadgeDefaults.compactContentPadding,
                                iconSize = LiftAppBadgeDefaults.compactIconSize,
                            )
                        },
                )
        )
    }
}
