package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

private val LightColorScheme =
    lightColorScheme(
        primary = Colors.Light.primary,
        onPrimary = Colors.Light.onPrimary,
        primaryContainer = Colors.Light.primaryContainer,
        onPrimaryContainer = Colors.Light.onPrimaryContainer,
        secondary = Colors.Light.secondary,
        onSecondary = Colors.Light.onSecondary,
        secondaryContainer = Colors.Light.secondaryContainer,
        onSecondaryContainer = Colors.Light.onSecondaryContainer,
        tertiary = Colors.Light.tertiary,
        onTertiary = Colors.Light.onTertiary,
        tertiaryContainer = Colors.Light.tertiaryContainer,
        onTertiaryContainer = Colors.Light.onTertiaryContainer,
        error = Colors.Light.error,
        errorContainer = Colors.Light.errorContainer,
        onError = Colors.Light.onError,
        onErrorContainer = Colors.Light.onErrorContainer,
        background = Colors.Light.background,
        onBackground = Colors.Light.onBackground,
        surface = Colors.Light.surface,
        onSurface = Colors.Light.onSurface,
        surfaceVariant = Colors.Light.surfaceVariant,
        onSurfaceVariant = Colors.Light.onSurfaceVariant,
        outline = Colors.Light.outline,
        inverseOnSurface = Colors.Light.inverseOnSurface,
        inverseSurface = Colors.Light.inverseSurface,
        inversePrimary = Colors.Light.inversePrimary,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Colors.Dark.primary,
        onPrimary = Colors.Dark.onPrimary,
        primaryContainer = Colors.Dark.primaryContainer,
        onPrimaryContainer = Colors.Dark.onPrimaryContainer,
        secondary = Colors.Dark.secondary,
        onSecondary = Colors.Dark.onSecondary,
        secondaryContainer = Colors.Dark.secondaryContainer,
        onSecondaryContainer = Colors.Dark.onSecondaryContainer,
        tertiary = Colors.Dark.tertiary,
        onTertiary = Colors.Dark.onTertiary,
        tertiaryContainer = Colors.Dark.tertiaryContainer,
        onTertiaryContainer = Colors.Dark.onTertiaryContainer,
        error = Colors.Dark.error,
        errorContainer = Colors.Dark.errorContainer,
        onError = Colors.Dark.onError,
        onErrorContainer = Colors.Dark.onErrorContainer,
        background = Colors.Dark.background,
        onBackground = Colors.Dark.onBackground,
        surface = Colors.Dark.surface,
        onSurface = Colors.Dark.onSurface,
        surfaceVariant = Colors.Dark.surfaceVariant,
        onSurfaceVariant = Colors.Dark.onSurfaceVariant,
        outline = Colors.Dark.outline,
        inverseOnSurface = Colors.Dark.inverseOnSurface,
        inverseSurface = Colors.Dark.inverseSurface,
        inversePrimary = Colors.Dark.inversePrimary,
    )

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
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val dimens = if (isLandscape) LandscapeDimens else PortraitDimens
    val liftAppColorScheme = getLiftAppColorScheme(darkTheme)

    MaterialTheme(colorScheme = colorScheme, typography = LiftAppTypography, shapes = Shapes) {
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
