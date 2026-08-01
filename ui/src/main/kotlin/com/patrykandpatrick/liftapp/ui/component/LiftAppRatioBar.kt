package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
 * Two quantities drawn to scale against each other, for a pair whose comparison is the point rather
 * than either value on its own. [leadingFraction] is the leading quantity's portion of the two, so
 * bars of visibly different length mean an imbalance — and two of the same length mean there is
 * none, which is worth showing just as plainly.
 *
 * The bars are centered in whatever height [modifier] gives them, so the pair can share a row's
 * height with taller neighbors without being stretched.
 */
@Composable
fun LiftAppRatioBar(
    leadingFraction: Float,
    leadingColor: Color,
    trailingColor: Color,
    modifier: Modifier = Modifier,
    thickness: Dp = LiftAppRatioBarDefaults.thickness,
    spacing: Dp = LiftAppRatioBarDefaults.spacing,
) {
    // A weight of zero is rejected outright rather than laid out as nothing, so a quantity holding
    // none of the pair is drawn as the thinnest bar the row will give it: a sliver reads as
    // "almost none", where a crash reads as nothing at all.
    val fraction = leadingFraction.coerceIn(MinFraction, 1f - MinFraction)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = modifier,
    ) {
        Bar(color = leadingColor, thickness = thickness, weight = fraction)
        Bar(color = trailingColor, thickness = thickness, weight = 1f - fraction)
    }
}

@Composable
private fun RowScope.Bar(color: Color, thickness: Dp, weight: Float) {
    Box(Modifier.weight(weight).height(thickness).background(color = color, shape = PillShape))
}

object LiftAppRatioBarDefaults {
    val thickness: Dp
        @Composable get() = dimens.ratioBar.thickness

    val spacing: Dp
        @Composable get() = dimens.ratioBar.spacing
}

/** The least of the total either bar is drawn as holding, so that neither is weightless. */
private const val MinFraction = .02f

@LightAndDarkThemePreview
@Composable
private fun LiftAppRatioBarPreview() {
    LiftAppTheme {
        LiftAppBackground {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                // The last is the degenerate pair: one side holding all of it.
                listOf(.5f, .4974f, .65f, 1f).forEach { fraction ->
                    LiftAppRatioBar(
                        leadingFraction = fraction,
                        leadingColor = colorScheme.chartColors[0],
                        trailingColor = colorScheme.chartColors[1],
                        modifier = Modifier.fillMaxWidth().height(20.dp),
                    )
                }
            }
        }
    }
}
