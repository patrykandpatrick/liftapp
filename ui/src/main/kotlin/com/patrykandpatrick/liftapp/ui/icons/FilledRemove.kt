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

val LiftAppIcons.FilledRemove: ImageVector
    get() =
        _FilledRemove
            ?: ImageVector.Builder(
                    name = "FilledRemove",
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
                        moveTo(6.5f, 9f)
                        curveTo(6.22386f, 9f, 6f, 9.22386f, 6f, 9.5f)
                        verticalLineTo(10.5f)
                        curveTo(6f, 10.7761f, 6.22386f, 11f, 6.5f, 11f)
                        horizontalLineTo(13.5f)
                        curveTo(13.7761f, 11f, 14f, 10.7761f, 14f, 10.5f)
                        verticalLineTo(9.5f)
                        curveTo(14f, 9.22386f, 13.7761f, 9f, 13.5f, 9f)
                        horizontalLineTo(6.5f)
                        close()
                    }
                }
                .build()
                .also { _FilledRemove = it }

private var _FilledRemove: ImageVector? = null

@LightAndDarkThemePreview
@Composable
private fun FilledRemovePreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.FilledRemove, null) } }
}
