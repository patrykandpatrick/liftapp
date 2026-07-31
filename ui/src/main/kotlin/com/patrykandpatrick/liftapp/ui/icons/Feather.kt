package com.patrykandpatrick.liftapp.ui.icons

/*
 * Feather from Lucide, derived from Feather Icons.
 *
 * Copyright (c) 2013-present Cole Bemis
 * Licensed under the MIT License.
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

val LiftAppIcons.Feather: ImageVector by lazy {
    ImageVector.Builder(
            name = "feather",
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
                moveTo(14.086f, 18.412f)
                arcToRelative(2f, 2f, 0f, false, true, -1.416f, 0.588f)
                horizontalLineTo(5f)
                verticalLineToRelative(-7.672f)
                arcToRelative(2f, 2f, 0f, false, true, 0.586f, -1.414f)
                lineTo(11.75f, 3.75f)
                arcToRelative(6f, 6f, 0f, true, true, 8.49f, 8.49f)
                close()
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(16f, 8f)
                lineTo(2f, 22f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(17.488f, 15f)
                horizontalLineTo(9f)
            }
        }
        .build()
}

@Composable
@LightAndDarkThemePreview
private fun FeatherPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.Feather, null) } }
}
