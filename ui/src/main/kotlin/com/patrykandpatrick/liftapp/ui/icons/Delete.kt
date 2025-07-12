/*
 * Converted using https://composables.com/svg-to-compose
 */
package com.patrykandpatrick.liftapp.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LiftAppIcons.Delete: ImageVector
    get() =
        _Delete
            ?: ImageVector.Builder(
                    name = "Delete",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                )
                .apply {
                    path(fill = SolidColor(Color.White)) {
                        moveTo(15f, 10.5f)
                        horizontalLineTo(8f)
                        verticalLineTo(18f)
                        curveTo(8f, 18.3728f, 8.11962f, 18.6059f, 8.25684f, 18.7432f)
                        curveTo(8.39406f, 18.8804f, 8.62725f, 19f, 9f, 19f)
                        horizontalLineTo(14f)
                        curveTo(14.3728f, 19f, 14.6059f, 18.8804f, 14.7432f, 18.7432f)
                        curveTo(14.8804f, 18.6059f, 15f, 18.3728f, 15f, 18f)
                        verticalLineTo(10.5f)
                        close()
                        moveTo(13.5f, 4f)
                        curveTo(14.0523f, 4f, 14.5f, 4.44772f, 14.5f, 5f)
                        horizontalLineTo(17f)
                        curveTo(17.5523f, 5f, 18f, 5.44772f, 18f, 6f)
                        curveTo(18f, 6.55228f, 17.5523f, 7f, 17f, 7f)
                        horizontalLineTo(6f)
                        curveTo(5.44772f, 7f, 5f, 6.55228f, 5f, 6f)
                        curveTo(5f, 5.44772f, 5.44772f, 5f, 6f, 5f)
                        horizontalLineTo(8.5f)
                        curveTo(8.5f, 4.44772f, 8.94772f, 4f, 9.5f, 4f)
                        horizontalLineTo(13.5f)
                        close()
                        moveTo(17f, 18f)
                        curveTo(17f, 18.8272f, 16.72f, 19.5944f, 16.1572f, 20.1572f)
                        curveTo(15.5944f, 20.72f, 14.8272f, 21f, 14f, 21f)
                        horizontalLineTo(9f)
                        curveTo(8.17275f, 21f, 7.40555f, 20.72f, 6.84277f, 20.1572f)
                        curveTo(6.27999f, 19.5944f, 6f, 18.8272f, 6f, 18f)
                        verticalLineTo(9.5f)
                        curveTo(6f, 8.94772f, 6.44772f, 8.5f, 7f, 8.5f)
                        horizontalLineTo(16f)
                        curveTo(16.2652f, 8.5f, 16.5195f, 8.60543f, 16.707f, 8.79297f)
                        curveTo(16.8946f, 8.98051f, 17f, 9.23478f, 17f, 9.5f)
                        verticalLineTo(18f)
                        close()
                    }
                }
                .build()
                .also { _Delete = it }

private var _Delete: ImageVector? = null
