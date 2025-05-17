package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors

data class ColorScheme(
    val primary: Color,
    val primaryDisabled: Color,
    val onPrimary: Color,
    val primaryHighlight: Color,
    val primaryHighlightActivated: Color,
    val secondary: Color,
    val secondaryDisabled: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val highlight: Color = primary,
    val isDarkColorScheme: Boolean,
    val borderColors: InteractiveBorderColors = getInteractiveBorderColors(highlight, outline),
    val onBackground: Color = onSurface,
)

private fun getInteractiveBorderColors(highlight: Color, outline: Color): InteractiveBorderColors =
    InteractiveBorderColors(outline, highlight, highlight)

private val LightColorScheme =
    ColorScheme(
        primary = Color(0xFF3A3AFF),
        primaryDisabled = Color(0x243A3AFF),
        onPrimary = Color(0xFFF0F0FF),
        primaryHighlight = Color.Transparent,
        primaryHighlightActivated = Color(0xFF010162),
        secondary = Color(0xFF000000),
        secondaryDisabled = Color(0xA8000000),
        background = Color(0xFFF0F0FF),
        surface = Color(0xFFF6F6FF),
        onSurface = Color(0xFF14143D),
        onSurfaceVariant = Color(0xFF171754),
        outline = Color(0x4A0000B0),
        isDarkColorScheme = false,
    )

private val DarkColorScheme =
    ColorScheme(
        primary = Color(0xFF5252E5),
        primaryDisabled = Color(0x3D5252E5),
        onPrimary = Color(0xFFF0F0FF),
        primaryHighlight = Color(0x6FF0F0FF),
        primaryHighlightActivated = Color(0xFFF0F0FF),
        secondary = Color(0xFFFFFFFF),
        secondaryDisabled = Color(0xC8FFFFFF),
        background = Color(0xFF0A0A0F),
        surface = Color(0xFF121216),
        onSurface = Color(0xFFE5E5FF),
        onSurfaceVariant = Color(0xFFC8C8EC),
        outline = Color(0x4FF0F0FF),
        isDarkColorScheme = true,
    )

fun getLiftAppColorScheme(isDarkTheme: Boolean): ColorScheme =
    if (isDarkTheme) DarkColorScheme else LightColorScheme

val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }

val colorScheme: ColorScheme
    @Composable get() = LocalColorScheme.current
