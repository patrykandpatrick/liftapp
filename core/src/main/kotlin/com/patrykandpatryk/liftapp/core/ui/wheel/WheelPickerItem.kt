package com.patrykandpatryk.liftapp.core.ui.wheel

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun WheelPickerScope.WheelPickerItem(value: String, modifier: Modifier = Modifier) {
    val deselectedColor = MaterialTheme.colorScheme.onSurface
    val selectedColor = MaterialTheme.colorScheme.primary

    val positionOffset = remember { mutableFloatStateOf(1f) }

    val textColor = remember { mutableStateOf(deselectedColor) }
    Text(
        text = value,
        style = MaterialTheme.typography.bodyLarge,
        color = textColor.value,
        modifier =
            modifier
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .onPositionChange { offset, viewPortOffset ->
                    positionOffset.floatValue = abs(viewPortOffset)
                    textColor.value = lerp(selectedColor, deselectedColor, abs(offset))
                }
                .graphicsLayer {
                    alpha = 0f + (1 - positionOffset.floatValue) // * .75f
                    scaleY = .5f + (1 - positionOffset.floatValue) * .5f
                },
    )
}
