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

val LiftAppIcons.ArrowForward: ImageVector
    get() =
        arrowForward
            ?: ImageVector.Builder(
                    name = "ArrowForward",
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
                    moveTo(19f, 12f)
                    lineTo(13.5f, 6.5f)
                    moveTo(19f, 12f)
                    lineTo(13.5f, 17.5f)
                    moveTo(19f, 12f)
                    horizontalLineTo(4.5f)
                }
                .build()
                .also { arrowForward = it }

private var arrowForward: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun ArrowForwardPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.ArrowForward, null) } }
}
