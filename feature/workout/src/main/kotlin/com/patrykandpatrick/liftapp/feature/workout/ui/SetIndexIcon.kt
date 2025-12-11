package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme

@Composable
fun SetIndexIcon(setIndex: Int, isCompleted: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(6.dp)
    val color = colorScheme.onSurfaceVariant
    Box(
        modifier =
            modifier.padding(8.dp).size(24.dp).drawBehind {
                rotate(45f) {
                    drawOutline(
                        outline = shape.createOutline(size, layoutDirection, this),
                        color = color,
                        style =
                            if (isCompleted) {
                                Fill
                            } else {
                                Stroke(width = 1.dp.toPx())
                            },
                    )
                }
            }
    ) {
        LiftAppText(
            text = "${setIndex + 1}",
            style = MaterialTheme.typography.bodySmall,
            color =
                if (isCompleted) {
                    colorScheme.surface
                } else {
                    colorScheme.onSurface
                },
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@LightAndDarkThemePreview
@Composable
private fun SetIndexIconPreview() {
    PreviewTheme {
        LiftAppBackground {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SetIndexIcon(setIndex = 0, isCompleted = true)
                SetIndexIcon(setIndex = 1, isCompleted = false)
            }
        }
    }
}
