package com.patrykandpatryk.liftapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Orange300,
    onPrimary = Color.Black,
    secondary = Orange500,
    onSecondary = Color.Black,
    tertiary = Orange700,
    onTertiary = Color.White,
    background = Black700,
    surface = Black900,
)

private val LightColorScheme = lightColorScheme(
    primary = Orange500,
    onPrimary = Color.Black,
    secondary = Orange700,
    onSecondary = Color.White,
    tertiary = Orange700,
    onTertiary = Color.White,
    background = White700,
    surface = Color.White,
)

@Composable
fun LiftAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
