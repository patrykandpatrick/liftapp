package com.patrykandpatryk.liftapp.core.ui.wheel

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.math.abs

@Composable
fun WheelPickerItem(
    text: String,
    index: Int,
    pickerValue: Float,
    pickerBufferSize: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color =
            lerp(
                colorScheme.primary,
                colorScheme.onSurface,
                abs(pickerValue - index).coerceIn(0f, 1f),
            ),
        modifier =
            modifier.padding(24.dp, 8.dp).graphicsLayer {
                val offsetFraction = abs(pickerValue - index) / pickerBufferSize
                alpha = 1 - offsetFraction
                scaleY = 1 - offsetFraction / 2
            },
    )
}
