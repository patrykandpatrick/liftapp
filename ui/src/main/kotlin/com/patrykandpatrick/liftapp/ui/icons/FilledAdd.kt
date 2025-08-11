/*
 * Converted using https://composables.com/svg-to-compose
 */
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

val LiftAppIcons.FilledAdd: ImageVector
    get() =
        _FilledAdd
            ?: ImageVector.Builder(
                    name = "FilledAdd",
                    defaultWidth = 20.dp,
                    defaultHeight = 20.dp,
                    viewportWidth = 20f,
                    viewportHeight = 20f,
                )
                .apply {
                    path(fill = SolidColor(Color.White)) {
                        moveTo(10f, 3f)
                        curveTo(13.866f, 3f, 17f, 6.13401f, 17f, 10f)
                        curveTo(17f, 13.866f, 13.866f, 17f, 10f, 17f)
                        curveTo(6.13401f, 17f, 3f, 13.866f, 3f, 10f)
                        curveTo(3f, 6.13401f, 6.13401f, 3f, 10f, 3f)
                        close()
                        moveTo(9.5f, 6f)
                        curveTo(9.22386f, 6f, 9f, 6.22386f, 9f, 6.5f)
                        verticalLineTo(9f)
                        horizontalLineTo(6.5f)
                        curveTo(6.22386f, 9f, 6f, 9.22386f, 6f, 9.5f)
                        verticalLineTo(10.5f)
                        curveTo(6f, 10.7761f, 6.22386f, 11f, 6.5f, 11f)
                        horizontalLineTo(9f)
                        verticalLineTo(13.5f)
                        curveTo(9f, 13.7761f, 9.22386f, 14f, 9.5f, 14f)
                        horizontalLineTo(10.5f)
                        curveTo(10.7761f, 14f, 11f, 13.7761f, 11f, 13.5f)
                        verticalLineTo(11f)
                        horizontalLineTo(13.5f)
                        curveTo(13.7761f, 11f, 14f, 10.7761f, 14f, 10.5f)
                        verticalLineTo(9.5f)
                        curveTo(14f, 9.22386f, 13.7761f, 9f, 13.5f, 9f)
                        horizontalLineTo(11f)
                        verticalLineTo(6.5f)
                        curveTo(11f, 6.22386f, 10.7761f, 6f, 10.5f, 6f)
                        horizontalLineTo(9.5f)
                        close()
                    }
                }
                .build()
                .also { _FilledAdd = it }

private var _FilledAdd: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun FilledAddPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.FilledAdd, null) } }
}
