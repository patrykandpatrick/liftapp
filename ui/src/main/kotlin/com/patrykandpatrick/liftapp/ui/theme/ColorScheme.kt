package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.modifier.InteractiveBorderColors

data class ColorScheme(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val outline: Color,
    val highlight: Color = primary,
    val interactiveBorderColors: InteractiveBorderColors =
        getInteractiveBorderColors(highlight, outline),
)

private fun getInteractiveBorderColors(highlight: Color, outline: Color): InteractiveBorderColors =
    InteractiveBorderColors(outline, highlight, highlight)

val LightColorScheme =
    ColorScheme(
        primary = Color(0xFF3B3BE5),
        secondary = Color(0xFF000000),
        background = Color(0xFFF0F0FF),
        surface = Color(0xFFF6F6FF),
        outline = Color(0x320000B0),
    )

val DarkColorScheme =
    ColorScheme(
        primary = Color(0xFF3B3BE5),
        secondary = Color(0xFFFFFFFF),
        background = Color(0xFF0A0A0F),
        surface = Color(0xFF121216),
        outline = Color(0xFF313138),
    )

fun getLiftAppColorScheme(isDarkTheme: Boolean): ColorScheme =
    if (isDarkTheme) DarkColorScheme else LightColorScheme

val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }

val colorScheme: ColorScheme
    @Composable get() = LocalColorScheme.current
