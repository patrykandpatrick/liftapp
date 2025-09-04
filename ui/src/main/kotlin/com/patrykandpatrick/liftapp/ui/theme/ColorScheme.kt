package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors

data class ColorScheme(
    val primary: Color,
    val primaryDisabled: Color,
    val onPrimary: Color,
    val onPrimaryOutline: Color,
    val onPrimaryDisabled: Color,
    val primaryHighlight: Color,
    val secondary: Color,
    val secondaryDisabled: Color,
    val onSecondary: Color,
    val onSecondaryDisabled: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val highlight: Color = primary,
    val error: Color,
    val onError: Color,
    val isDarkColorScheme: Boolean,
    val chartColors: ChartColors,
    val borderColors: InteractiveBorderColors = getInteractiveBorderColors(highlight, outline),
    val onBackground: Color = onSurface,
)

data class ChartColors(
    val series1: Color,
    val series2: Color,
    val series3: Color,
    val series4: Color,
    val series5: Color,
)

private fun getInteractiveBorderColors(highlight: Color, outline: Color): InteractiveBorderColors =
    InteractiveBorderColors(outline, highlight, highlight)

private val LightColorScheme =
    ColorScheme(
        primary = Color(0xFF5151FA),
        primaryDisabled = Color(0x243A3AFF),
        onPrimary = Color(0xFFE0E0FF),
        onPrimaryDisabled = Color(0xFF21213D),
        onPrimaryOutline = Color(0xFF1B1B9A),
        primaryHighlight = Color.Transparent,
        secondary = Color(0xFFFFAB00),
        secondaryDisabled = Color(0xFFBEAA83),
        onSecondary = Color(0xFF2A1500),
        onSecondaryDisabled = Color(0xFF4D2A08),
        background = Color(0xFFF2F2FF),
        surface = Color(0xFFF6F6FF),
        surfaceVariant = Color.White,
        onSurface = Color(0xFF00002F),
        onSurfaceVariant = Color(0xFF171754),
        outline = Color(0xFF9595A6),
        error = Color(color = 0xff880000),
        onError = Color(color = 0xFFFFF1F1),
        chartColors =
            ChartColors(
                Color(0xFF5151FA),
                Color(0xFFD654F0),
                Color(0xFFF054A2),
                Color(0xFFF06154),
                Color(0xFFF08854),
            ),
        isDarkColorScheme = false,
    )

private val DarkColorScheme =
    ColorScheme(
        primary = Color(0xFF7878EF),
        primaryDisabled = Color(0x3D5252E5),
        onPrimary = Color(0xFF00002C),
        onPrimaryDisabled = Color(0xFF9797DC),
        onPrimaryOutline = Color(0xFFD3D3FF),
        primaryHighlight = Color(0x6FF0F0FF),
        secondary = Color(0xFFFFAB00),
        secondaryDisabled = Color(0xFF6E5119),
        onSecondary = Color(0xFF3A1D00),
        onSecondaryDisabled = Color(0xFFECC8A3),
        background = Color(0xFF0A0A0F),
        surface = Color(0xFF121216),
        surfaceVariant = Color(0xFF252534),
        onSurface = Color(0xFFE5E5FF),
        onSurfaceVariant = Color(0xFFC8C8EC),
        outline = Color(0x4FE1E1F8),
        error = Color(color = 0xffff4444),
        onError = Color(color = 0xFF330000),
        chartColors =
            ChartColors(
                Color(0xFF6C6CF0),
                Color(0xFFDA6CF0),
                Color(0xFFF06CAE),
                Color(0xFFF06C6C),
                Color(0xFFF0986C),
            ),
        isDarkColorScheme = true,
    )

fun getLiftAppColorScheme(isDarkTheme: Boolean): ColorScheme =
    if (isDarkTheme) DarkColorScheme else LightColorScheme

val LocalColorScheme = staticCompositionLocalOf { LightColorScheme }

val colorScheme: ColorScheme
    @Composable get() = LocalColorScheme.current
