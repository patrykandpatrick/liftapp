package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme as MaterialColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.ui.dimens.LandscapeDimens
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.PortraitDimens
import com.patrykandpatrick.liftapp.ui.isLandscape
import com.patrykandpatrick.liftapp.ui.modifier.ScaleIndication
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.VicoTheme

data object Alpha {
    const val disabled: Float = 0.38f
    const val standard: Float = 1f
    const val unfocused: Float = 0.6f

    @Stable
    fun get(enabled: Boolean = true, focused: Boolean = true): Float =
        when {
            !enabled -> disabled
            !focused -> unfocused
            else -> standard
        }
}

val Color.disabled: Color
    get() = copy(alpha = Alpha.disabled)

val Color.unfocused: Color
    get() = copy(alpha = Alpha.unfocused)

@Composable
fun LiftAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val dimens = if (isLandscape) LandscapeDimens else PortraitDimens
    val liftAppColorScheme = getLiftAppColorScheme(darkTheme)

    MaterialTheme(
        colorScheme = liftAppColorScheme.toMaterialColorScheme(),
        typography = LiftAppTypography,
        shapes = Shapes,
    ) {
        CompositionLocalProvider(
            LocalDimens provides dimens,
            LocalColorScheme provides liftAppColorScheme,
            LocalIndication provides ScaleIndication(),
        ) {
            ProvideVicoTheme(theme = getVicoTheme(liftAppColorScheme), content = content)
        }
    }
}

private fun getVicoTheme(colorScheme: ColorScheme): VicoTheme =
    VicoTheme(
        candlestickCartesianLayerColors =
            VicoTheme.CandlestickCartesianLayerColors(
                colorScheme.green,
                neutral = colorScheme.outline,
                bearish = colorScheme.red,
            ),
        columnCartesianLayerColors = colorScheme.chartColors,
        lineCartesianLayerColors = colorScheme.chartColors,
        lineColor = colorScheme.divider,
        textColor = colorScheme.onSurface,
    )

/**
 * The palette Material's own components read.
 *
 * Everything the app draws itself goes through [ColorScheme]. This exists for the Material
 * components still in use — the switch, the radio button, the date and time pickers, the snackbar,
 * the text fields, the dividers — which resolve their colors from `MaterialTheme.colorScheme` and
 * cannot see the app's palette. It replaces a red scheme left over from the app's previous look,
 * which is why a switch used to come out red.
 *
 * Every role is filled, including the ones nothing reads today, so that no component can quietly
 * fall back to Material's baseline purple.
 */
private fun ColorScheme.toMaterialColorScheme(): MaterialColorScheme =
    // Both builders take the same roles, and every one is given below, so whichever seeds the
    // values makes no difference to the result.
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        // The tonal fill a checked card wears, which is also what Material tints a selected time
        // in the picker with.
        primaryContainer = primaryDisabled,
        onPrimaryContainer = onSurface,
        // The snackbar's action, read against `inverseSurface`: light on dark, and dark on light.
        inversePrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        // Material tints a selected date range with this. Keeping it on the primary tint stops the
        // pickers reaching for an accent the app does not have.
        secondaryContainer = primaryDisabled,
        onSecondaryContainer = onSurface,
        tertiary = secondary,
        onTertiary = onSecondary,
        tertiaryContainer = primaryDisabled,
        onTertiaryContainer = onSurface,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        // Nothing. Material tints a raised `Surface` with this, which gave the dialogs still on
        // `DialogContent` a blue cast that `LiftAppAlertDialog` — a flat `LiftAppCard` on
        // `surface` — does not have. Transparent leaves an elevated surface exactly `surface`.
        surfaceTint = Color.Transparent,
        inverseSurface = onSurface,
        inverseOnSurface = surface,
        error = error,
        onError = onError,
        errorContainer = error,
        // Only ever read as a hovered outline or label, drawn on an ordinary surface rather than on
        // `errorContainer`, so the error color is what actually reads there.
        onErrorContainer = error,
        outline = outline,
        // Material draws its dividers with this one.
        outlineVariant = divider,
        scrim = bottomSheetScrim,
        // The palette has no neutral above `surfaceVariant`, and it does not need one: an unchecked
        // switch track drawn in it comes out hollow inside an `outline` border, which is how the
        // app's own checkbox and radio button already read.
        surfaceBright = surfaceVariant,
        surfaceContainer = surface,
        surfaceContainerHigh = surfaceVariant,
        surfaceContainerHighest = surfaceVariant,
        surfaceContainerLow = surface,
        surfaceContainerLowest = background,
        surfaceDim = background,
        // Nothing in the app reads the fixed roles. They are filled so that nothing can start to
        // without it showing up as a stray purple.
        primaryFixed = primaryDisabled,
        primaryFixedDim = primaryDisabled,
        onPrimaryFixed = onSurface,
        onPrimaryFixedVariant = onSurfaceVariant,
        secondaryFixed = primaryDisabled,
        secondaryFixedDim = primaryDisabled,
        onSecondaryFixed = onSurface,
        onSecondaryFixedVariant = onSurfaceVariant,
        tertiaryFixed = primaryDisabled,
        tertiaryFixedDim = primaryDisabled,
        onTertiaryFixed = onSurface,
        onTertiaryFixedVariant = onSurfaceVariant,
    )
