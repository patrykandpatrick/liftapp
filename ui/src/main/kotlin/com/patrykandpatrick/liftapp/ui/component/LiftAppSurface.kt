package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
@NonRestartableComposable
fun LiftAppBackground(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = colorScheme.background,
    contentColor: Color = colorScheme.onBackground,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        content = content,
    )
}
