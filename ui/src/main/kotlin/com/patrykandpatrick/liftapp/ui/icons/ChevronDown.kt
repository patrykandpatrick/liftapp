package com.patrykandpatrick.liftapp.ui.icons

/*
 * Chevron Down from Lucide.
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

val LiftAppIcons.ChevronDown: ImageVector by lazy {
    ImageVector.Builder(
            name = "chevron-down",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        .path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(6f, 9f)
            lineToRelative(6f, 6f)
            lineToRelative(6f, -6f)
        }
        .build()
}

@LightAndDarkThemePreview
@Composable
private fun ChevronDownPreview() {
    LiftAppTheme { LiftAppBackground { Icon(LiftAppIcons.ChevronDown, null) } }
}
