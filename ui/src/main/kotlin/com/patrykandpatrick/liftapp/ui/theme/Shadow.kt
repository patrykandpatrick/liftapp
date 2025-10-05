package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.bottomSheetShadow(shape: Shape = BottomSheetShape): Modifier =
    this.dropShadow(shape) {
            radius = 1.dp.toPx()
            spread = 1.dp.toPx()
            offset = Offset(0f, -1f)
            color = Color.Black.copy(alpha = 0.16f)
        }
        .dropShadow(shape) {
            radius = 8.dp.toPx()
            color = Color.Black.copy(alpha = 0.24f)
        }
