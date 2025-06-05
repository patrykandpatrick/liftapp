package com.patrykandpatrick.liftapp.ui.icons

/*
 * Converted using https://composables.com/svg-to-compose
 */

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

val LiftAppIcons.ArrowBack: ImageVector
    get() =
        arrowBack
            ?: ImageVector.Builder(
                    name = "ArrowBack",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .path(fill = SolidColor(Color.White)) {
                    moveTo(4.5f, 12f)
                    lineTo(3.79289f, 11.2929f)
                    curveTo(3.40237f, 11.6834f, 3.40237f, 12.3166f, 3.79289f, 12.7071f)
                    lineTo(4.5f, 12f)
                    close()
                    moveTo(10.7071f, 7.20711f)
                    curveTo(11.0976f, 6.81658f, 11.0976f, 6.18342f, 10.7071f, 5.79289f)
                    curveTo(10.3166f, 5.40237f, 9.68342f, 5.40237f, 9.29289f, 5.79289f)
                    lineTo(10.7071f, 7.20711f)
                    close()
                    moveTo(9.29289f, 18.2071f)
                    curveTo(9.68342f, 18.5976f, 10.3166f, 18.5976f, 10.7071f, 18.2071f)
                    curveTo(11.0976f, 17.8166f, 11.0976f, 17.1834f, 10.7071f, 16.7929f)
                    lineTo(9.29289f, 18.2071f)
                    close()
                    moveTo(16f, 15f)
                    curveTo(15.4477f, 15f, 15f, 15.4477f, 15f, 16f)
                    curveTo(15f, 16.5523f, 15.4477f, 17f, 16f, 17f)
                    verticalLineTo(15f)
                    close()
                    moveTo(4.5f, 12f)
                    lineTo(5.20711f, 12.7071f)
                    lineTo(10.7071f, 7.20711f)
                    lineTo(10f, 6.5f)
                    lineTo(9.29289f, 5.79289f)
                    lineTo(3.79289f, 11.2929f)
                    lineTo(4.5f, 12f)
                    close()
                    moveTo(4.5f, 12f)
                    lineTo(3.79289f, 12.7071f)
                    lineTo(9.29289f, 18.2071f)
                    lineTo(10f, 17.5f)
                    lineTo(10.7071f, 16.7929f)
                    lineTo(5.20711f, 11.2929f)
                    lineTo(4.5f, 12f)
                    close()
                    moveTo(4.5f, 12f)
                    verticalLineTo(13f)
                    horizontalLineTo(17.5f)
                    verticalLineTo(12f)
                    verticalLineTo(11f)
                    horizontalLineTo(4.5f)
                    verticalLineTo(12f)
                    close()
                    moveTo(17.5f, 12f)
                    verticalLineTo(13f)
                    curveTo(18.0357f, 13f, 18.5f, 13.4557f, 18.5f, 14f)
                    horizontalLineTo(19.5f)
                    horizontalLineTo(20.5f)
                    curveTo(20.5f, 12.3352f, 19.1243f, 11f, 17.5f, 11f)
                    verticalLineTo(12f)
                    close()
                    moveTo(19.5f, 14f)
                    horizontalLineTo(18.5f)
                    curveTo(18.5f, 14.5443f, 18.0357f, 15f, 17.5f, 15f)
                    verticalLineTo(16f)
                    verticalLineTo(17f)
                    curveTo(19.1243f, 17f, 20.5f, 15.6648f, 20.5f, 14f)
                    horizontalLineTo(19.5f)
                    close()
                    moveTo(17.5f, 16f)
                    verticalLineTo(15f)
                    horizontalLineTo(16f)
                    verticalLineTo(16f)
                    verticalLineTo(17f)
                    horizontalLineTo(17.5f)
                    verticalLineTo(16f)
                    close()
                }
                .build()
                .also { arrowBack = it }

private var arrowBack: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun ArrowBackPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.ArrowBack, null) } }
}
