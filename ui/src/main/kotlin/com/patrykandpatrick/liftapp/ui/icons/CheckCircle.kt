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

val LiftAppIcons.CheckCircle: ImageVector
    get() =
        _checkCircle
            ?: ImageVector.Builder(
                    name = "checkcircle",
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
                        moveTo(7.5f, 12.5f)
                        lineTo(10.5f, 15.5f)
                        lineTo(16.5f, 9.5f)
                    }
                    path(stroke = SolidColor(Color.White), strokeLineWidth = 2f) {
                        moveTo(21f, 12f)
                        arcTo(9f, 9f, 0f, false, true, 12f, 21f)
                        arcTo(9f, 9f, 0f, false, true, 3f, 12f)
                        arcTo(9f, 9f, 0f, false, true, 21f, 12f)
                        close()
                    }
                }
                .build()
                .also { _checkCircle = it }

private var _checkCircle: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun CheckCirclePreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.CheckCircle, null) } }
}
