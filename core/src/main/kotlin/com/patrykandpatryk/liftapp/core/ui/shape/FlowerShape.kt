package com.patrykandpatryk.liftapp.core.ui.shape

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.extension.half
import com.patrykandpatrick.vico.core.model.Point
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlin.math.cos
import kotlin.math.sin

private const val FULL_CIRCLE_DEGREES = 360

private const val CENTER_TOP_ANGLE = 270.0

class FlowerShape(
    var waveCount: Int = 8,
    var waveHeight: Dp = 4.dp,
) : Shape {

    private val path = Path()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = with(density) {

        path.reset()

        val center = size.center

        val radius = minOf(size.width, size.height).half - waveHeight.toPx()

        path.reset()

        for (i in 0..FULL_CIRCLE_DEGREES) {

            val radians = Math.toRadians(i.toDouble())

            val sinRoundness = sin(radians.toFloat() * waveCount) * waveHeight.toPx()

            val (x, y) = translatePointByAngle(
                center = Point(
                    x = center.x,
                    y = center.y,
                ),
                point = Point(
                    x = center.x,
                    y = center.y - radius - sinRoundness,
                ),
                angle = radians + Math.toRadians(CENTER_TOP_ANGLE / waveCount),
            )

            if (path.isEmpty) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        path.close()

        Outline.Generic(path)
    }
}

fun translatePointByAngle(center: Point, point: Point, angle: Double): Point =
    Point(
        ((point.x - center.x) * cos(angle) - (point.y - center.y) * sin(angle) + center.x).toFloat(),
        ((point.y - center.y) * cos(angle) + (point.x - center.x) * sin(angle) + center.y).toFloat(),
    )

@LightAndDarkThemePreview
@Composable
fun FlowerShapePreview() {
    LiftAppTheme {
        Surface {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = FlowerShape(),
                    )
            )
        }
    }
}
