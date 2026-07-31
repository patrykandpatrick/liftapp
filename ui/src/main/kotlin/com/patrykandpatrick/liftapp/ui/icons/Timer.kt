package com.patrykandpatrick.liftapp.ui.icons

/*
 * Timer from Lucide.
 *
 * Copyright (c) 2025 Lucide Contributors
 * Licensed under the ISC License.
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

val LiftAppIcons.Timer: ImageVector by lazy {
    ImageVector.Builder(
            name = "timer",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        .apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(10f, 2f)
                horizontalLineTo(14f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 14f)
                lineTo(15f, 11f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(20f, 14f)
                arcToRelative(8f, 8f, 0f, false, true, -8f, 8f)
                arcToRelative(8f, 8f, 0f, false, true, -8f, -8f)
                arcToRelative(8f, 8f, 0f, false, true, 8f, -8f)
                arcToRelative(8f, 8f, 0f, false, true, 8f, 8f)
                close()
            }
        }
        .build()
}

@Composable
@LightAndDarkThemePreview
private fun TimerPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.Timer, null) } }
}
