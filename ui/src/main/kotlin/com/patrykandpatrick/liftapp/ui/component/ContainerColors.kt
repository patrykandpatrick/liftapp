package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.modifier.InteractiveBorderColors

data class ContainerColors(
    val backgroundColor: Color,
    val contentColor: Color,
    val interactiveBorderColors: InteractiveBorderColors,
    val disabledBackgroundColor: Color,
    val disabledContentColor: Color,
)
