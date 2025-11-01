package com.patrykandpatrick.liftapp.ui.icons

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

val LiftAppIcons.Edit: ImageVector
    get() =
        _edit
            ?: ImageVector.Builder(
                    name = "edit",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .apply {
                    path(stroke = SolidColor(Color.White), strokeLineWidth = 2f) {
                        moveTo(15f, 11.5f)
                        lineTo(7.5f, 19f)
                        horizontalLineTo(5f)
                        verticalLineTo(16.5f)
                        lineTo(12.5f, 9f)
                        lineTo(15f, 11.5f)
                        close()
                    }
                    path(stroke = SolidColor(Color.White), strokeLineWidth = 2f) {
                        moveTo(17.5f, 9f)
                        lineTo(15f, 6.5f)
                        lineTo(16.25f, 5.25f)
                        curveTo(16.9404f, 4.55964f, 18.0596f, 4.55964f, 18.75f, 5.25f)
                        curveTo(19.4404f, 5.94036f, 19.4404f, 7.05965f, 18.75f, 7.75f)
                        lineTo(17.5f, 9f)
                        close()
                    }
                }
                .build()
                .also { _edit = it }

private var _edit: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun ArrowBackPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.Edit, null) } }
}
