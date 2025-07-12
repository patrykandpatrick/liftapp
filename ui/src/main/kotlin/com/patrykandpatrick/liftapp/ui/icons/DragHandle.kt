/*
 * Converted using https://composables.com/svg-to-compose
 */
package com.patrykandpatrick.liftapp.ui.icons

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

val LiftAppIcons.DragHandle: ImageVector
    get() =
        _DragHandle
            ?: ImageVector.Builder(
                    name = "DragHandle",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .apply {
                    path(
                        stroke = SolidColor(Color.White),
                        strokeLineWidth = 2f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    ) {
                        moveTo(18f, 12f)
                        horizontalLineTo(6f)
                        moveTo(15f, 8f)
                        lineTo(12f, 5f)
                        lineTo(9f, 8f)
                        moveTo(15f, 16f)
                        lineTo(12f, 19f)
                        lineTo(9f, 16f)
                    }
                }
                .build()
                .also { _DragHandle = it }

private var _DragHandle: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun DragHandlePreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.DragHandle, null) } }
}
