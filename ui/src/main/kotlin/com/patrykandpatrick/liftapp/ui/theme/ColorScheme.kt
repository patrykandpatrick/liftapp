package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ColorScheme(
    val primary: Color,
    val primaryDisabled: Color,
    val onPrimary: Color,
    val onPrimaryOutline: Color,
    val onPrimaryDisabled: Color,
    val secondary: Color,
    val secondaryDisabled: Color,
    val onSecondary: Color,
    val onSecondaryDisabled: Color,
    val background: Color,
    val surface: Color,
    val foreground: Color,
    val foregroundVariant: Color,
    val outline: Color,
    val error: Color,
    val onError: Color,
    val isDarkColorScheme: Boolean,
    val green: Color,
    val yellow: Color,
    val orange: Color,
    val red: Color,
    val bottomSheetScrim: Color = Color.Black,
) {
    val chartColors = listOf(green, yellow, orange, red)
}

private val LightColorScheme =
    ColorScheme(
        primary = Color(0xFF5151FA),
        primaryDisabled = Color(0x243A3AFF),
        onPrimary = Color(0xFFE0E0FF),
        onPrimaryDisabled = Color(0xFF21213D),
        onPrimaryOutline = Color(0xFF1B1B9A),
        secondary = Color(0xFFFFAB00),
        secondaryDisabled = Color(0xFFBEAA83),
        onSecondary = Color(0xFF2A1500),
        onSecondaryDisabled = Color(0xFF4D2A08),
        background = TailwindColors.zinc100,
        surface = Color.White,
        foreground = TailwindColors.zinc950,
        foregroundVariant = TailwindColors.zinc700,
        outline = TailwindColors.zinc400,
        error = Color(color = 0xff880000),
        onError = Color(color = 0xFFFFF1F1),
        green = Color(0xFF25A98F),
        yellow = Color(0xFFF59F00),
        orange = Color(0xFFFF5900),
        red = Color(0xFFFF0040),
        isDarkColorScheme = false,
    )

private val DarkColorScheme =
    ColorScheme(
        primary = Color(0xFF7878EF),
        primaryDisabled = Color(0x3D5252E5),
        onPrimary = Color(0xFF00002C),
        onPrimaryDisabled = Color(0xFF9797DC),
        onPrimaryOutline = Color(0xFFD3D3FF),
        secondary = Color(0xFFFFAB00),
        secondaryDisabled = Color(0xFF6E5119),
        onSecondary = Color(0xFF3A1D00),
        onSecondaryDisabled = Color(0xFFECC8A3),
        background = Color.Black,
        surface = TailwindColors.zinc900,
        foreground = TailwindColors.zinc50,
        foregroundVariant = TailwindColors.zinc300,
        outline = TailwindColors.zinc700,
        error = Color(color = 0xffff4444),
        onError = Color(color = 0xFF330000),
        green = Color(0xFF00FFCC),
        yellow = Color(0xFFFFE100),
        orange = Color(0xFFFF5900),
        red = Color(0xFFFF0040),
        isDarkColorScheme = true,
    )

fun getLiftAppColorScheme(isDarkTheme: Boolean): ColorScheme =
    if (isDarkTheme) DarkColorScheme else LightColorScheme

val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }

val colorScheme: ColorScheme
    @Composable get() = LocalColorScheme.current
