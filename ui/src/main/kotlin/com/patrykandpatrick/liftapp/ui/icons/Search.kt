package com.patrykandpatrick.liftapp.ui.icons

/*
 * Search from Lucide.
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

val LiftAppIcons.Search: ImageVector by lazy {
    ImageVector.Builder(
            name = "search",
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
                moveTo(11f, 19f)
                arcToRelative(8f, 8f, 0f, true, true, 0f, -16f)
                arcToRelative(8f, 8f, 0f, true, true, 0f, 16f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(21f, 21f)
                lineTo(16.65f, 16.65f)
            }
        }
        .build()
}

@Composable
@LightAndDarkThemePreview
private fun SearchPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.Search, null) } }
}
