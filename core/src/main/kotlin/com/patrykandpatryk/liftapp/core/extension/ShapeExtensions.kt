package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density

@Suppress("UNCHECKED_CAST")
@Composable
fun <T : CornerBasedShape> T.scaleCornerSize(scale: Float): T =
    remember(scale) {
        copy(
            topStart = scaledCornerSize(topStart, scale),
            topEnd = scaledCornerSize(topEnd, scale),
            bottomEnd = scaledCornerSize(bottomEnd, scale),
            bottomStart = scaledCornerSize(bottomStart, scale),
        ) as T
    }

fun scaledCornerSize(base: CornerSize, scale: Float): CornerSize = object : CornerSize {

    override fun toPx(shapeSize: Size, density: Density): Float =
        base.toPx(shapeSize, density) * scale.coerceAtLeast(0f)
}
