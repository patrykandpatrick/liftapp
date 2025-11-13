package com.patrykandpatrick.liftapp.ui.modifier

import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.topTintedEdge(shape: Shape) =
    this.border(
        shape = shape,
        width = 1.dp,
        brush = Brush.verticalGradient(colors = listOf(Color.White.copy(.24f), Color.Transparent)),
    )

fun Modifier.bottomTintedEdge(shape: Shape) =
    this.border(
        shape = shape,
        width = 1.dp,
        brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(.24f))),
    )
