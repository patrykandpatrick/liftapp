package com.patrykandpatryk.liftapp.core.ui.wheel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun WheelPickerWindow(modifier: Modifier = Modifier) {
    Box(
        modifier
            .border(1.dp, colorScheme.primary, RoundedCornerShape(8.dp))
            .background(colorScheme.primaryDisabled, RoundedCornerShape(8.dp))
    )
}
