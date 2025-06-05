package com.patrykandpatrick.liftapp.ui.icons

/*
 * Converted using https://composables.com/svg-to-compose
 */

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

val LiftAppIcons.Cross: ImageVector
    get() =
        cross
            ?: ImageVector.Builder(
                    name = "Cross",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                ) {
                    moveTo(6f, 6f)
                    lineTo(18f, 18f)
                    moveTo(6f, 18f)
                    lineTo(18f, 6f)
                }
                .build()
                .also { cross = it }

private var cross: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun CrossPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.Cross, null) } }
}
