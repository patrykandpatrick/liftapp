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

val LiftAppIcons.MoreVertical: ImageVector
    get() =
        moreVertical
            ?: ImageVector.Builder(
                    name = "MoreVertical",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .apply {
                    path(fill = SolidColor(Color.White)) {
                        moveTo(12f, 16f)
                        curveTo(10.8954f, 16f, 10f, 16.8954f, 10f, 18f)
                        curveTo(10f, 19.1046f, 10.8954f, 20f, 12f, 20f)
                        curveTo(13.1046f, 20f, 14f, 19.1046f, 14f, 18f)
                        curveTo(14f, 16.8954f, 13.1046f, 16f, 12f, 16f)
                        close()
                        moveTo(12f, 10f)
                        curveTo(10.8954f, 10f, 10f, 10.8954f, 10f, 12f)
                        curveTo(10f, 13.1046f, 10.8954f, 14f, 12f, 14f)
                        curveTo(13.1046f, 14f, 14f, 13.1046f, 14f, 12f)
                        curveTo(14f, 10.8954f, 13.1046f, 10f, 12f, 10f)
                        close()
                        moveTo(12f, 4f)
                        curveTo(10.8954f, 4f, 10f, 4.89543f, 10f, 6f)
                        curveTo(10f, 7.10457f, 10.8954f, 8f, 12f, 8f)
                        curveTo(13.1046f, 8f, 14f, 7.10457f, 14f, 6f)
                        curveTo(14f, 4.89543f, 13.1046f, 4f, 12f, 4f)
                        close()
                    }
                }
                .build()
                .also { moreVertical = it }

private var moreVertical: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun ArrowBackPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.MoreVertical, null) } }
}
