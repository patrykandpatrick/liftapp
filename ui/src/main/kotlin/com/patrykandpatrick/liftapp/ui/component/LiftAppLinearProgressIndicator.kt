package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * Where a value sits in a range known up front, drawn as a filled portion of a track. [progress]
 * runs 0..1 and is clamped, so a value from outside the range reads as full or empty rather than
 * overrunning the track.
 *
 * Determinate only: there is nothing to show for a quantity with no ceiling, and a bar filled to an
 * invented maximum is a claim the caller cannot support.
 *
 * The bar is centered in whatever height [modifier] gives it, so it can share a row's height with
 * taller neighbors without being stretched.
 */
@Composable
fun LiftAppLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = colorScheme.primary,
    trackColor: Color = LiftAppLinearProgressIndicatorDefaults.trackColor,
    thickness: Dp = LiftAppLinearProgressIndicatorDefaults.thickness,
) {
    Box(contentAlignment = Alignment.CenterStart, modifier = modifier) {
        Box(Modifier.fillMaxWidth().height(thickness).background(color = trackColor, PillShape))

        Box(
            Modifier.fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(thickness)
                .background(color = color, shape = PillShape)
        )
    }
}

object LiftAppLinearProgressIndicatorDefaults {
    /** The track is a supporting boundary, so it uses the decorative outline variant. */
    val trackColor: Color
        @Composable get() = colorScheme.outline

    val thickness: Dp
        @Composable get() = dimens.progress.thickness
}

@LightAndDarkThemePreview
@Composable
private fun LiftAppLinearProgressIndicatorPreview() {
    LiftAppTheme {
        LiftAppBackground {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                listOf(0f, .16f, .42f, 1f).forEach { progress ->
                    LiftAppLinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth().height(20.dp),
                    )
                }
            }
        }
    }
}
