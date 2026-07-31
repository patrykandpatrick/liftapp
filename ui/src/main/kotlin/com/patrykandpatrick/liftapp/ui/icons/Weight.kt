package com.patrykandpatrick.liftapp.ui.icons

/*
 * Weight from Lucide.
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

val LiftAppIcons.Weight: ImageVector by lazy {
    ImageVector.Builder(
            name = "weight",
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
                moveTo(15f, 5f)
                arcToRelative(3f, 3f, 0f, true, true, -6f, 0f)
                arcToRelative(3f, 3f, 0f, true, true, 6f, 0f)
                close()
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(6.5f, 8f)
                arcToRelative(2f, 2f, 0f, false, false, -1.905f, 1.46f)
                lineTo(2.1f, 18.5f)
                arcToRelative(2f, 2f, 0f, false, false, 1.9f, 2.5f)
                horizontalLineToRelative(16f)
                arcToRelative(2f, 2f, 0f, false, false, 1.925f, -2.54f)
                lineTo(19.4f, 9.5f)
                arcToRelative(2f, 2f, 0f, false, false, -1.92f, -1.5f)
                close()
            }
        }
        .build()
}

@Composable
@LightAndDarkThemePreview
private fun WeightPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.Weight, null) } }
}
