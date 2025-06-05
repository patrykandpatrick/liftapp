package com.patrykandpatrick.liftapp.ui.icons

/*
 * Converted using https://composables.com/svg-to-compose
 */

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

val LiftAppIcons.ChevronForward: ImageVector
    get() =
        chevronForward
            ?: ImageVector.Builder(
                    name = "ChevronForward",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(9.5f, 7f)
                    lineTo(14.5f, 12f)
                    lineTo(9.5f, 17f)
                }
                .build()
                .also { chevronForward = it }

private var chevronForward: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun ChevronForwardPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.ChevronForward, null) } }
}
