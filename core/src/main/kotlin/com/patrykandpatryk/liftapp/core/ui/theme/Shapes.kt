package com.patrykandpatryk.liftapp.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.vico.core.component.shape.cornered.Corner
import com.patrykandpatryk.vico.core.component.shape.cornered.CorneredShape
import com.patrykandpatryk.vico.core.component.shape.cornered.CutCornerTreatment
import com.patrykandpatryk.vico.core.component.shape.cornered.RoundedCornerTreatment
import com.patrykandpatryk.vico.core.context.MutableMeasureContext

@Suppress("MagicNumber")
val PillShape = RoundedCornerShape(100.dp)

val BottomSheetShape = RoundedCornerShape(
    topStart = 16.dp,
    topEnd = 16.dp,
)

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
)

@Stable
val RoutineCardShape: Shape = run {
    val roundedCorner = Corner.Absolute(
        sizeDp = 8f,
        cornerTreatment = RoundedCornerTreatment,
    )

    object :
        CorneredShape(
            topLeft = Corner.Absolute(
                sizeDp = 16f,
                cornerTreatment = CutCornerTreatment,
            ),
            topRight = roundedCorner,
            bottomLeft = roundedCorner,
            bottomRight = roundedCorner,
        ),
        Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val path = Path()

            createPath(
                context = MutableMeasureContext(
                    canvasBounds = size.toRect().toAndroidRectF(),
                    density = density.density,
                    fontScale = density.density * density.fontScale,
                    isLtr = layoutDirection == LayoutDirection.Ltr,
                ),
                path = path.asAndroidPath(),
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
            )
            return Outline.Generic(path)
        }
    }
}
