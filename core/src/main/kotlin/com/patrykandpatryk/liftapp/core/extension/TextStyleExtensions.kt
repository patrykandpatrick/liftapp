package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun TextStyle.withColor(block: ColorScheme.() -> Color) =
    copy(color = block(MaterialTheme.colorScheme))
