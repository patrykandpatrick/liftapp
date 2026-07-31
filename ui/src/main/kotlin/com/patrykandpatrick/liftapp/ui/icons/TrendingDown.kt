package com.patrykandpatrick.liftapp.ui.icons

/*
 * Trending Down from Lucide.
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

val LiftAppIcons.TrendingDown: ImageVector by lazy {
    ImageVector.Builder(
            name = "trending-down",
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
                moveTo(22f, 17f)
                lineTo(13.5f, 8.5f)
                lineTo(8.5f, 13.5f)
                lineTo(2f, 7f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(16f, 17f)
                horizontalLineTo(22f)
                verticalLineTo(11f)
            }
        }
        .build()
}

@Composable
@LightAndDarkThemePreview
private fun TrendingDownPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.TrendingDown, null) } }
}
