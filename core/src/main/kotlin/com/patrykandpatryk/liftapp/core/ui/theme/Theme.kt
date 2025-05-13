package com.patrykandpatryk.liftapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.ui.theme.LocalColorScheme
import com.patrykandpatrick.liftapp.ui.theme.getLiftAppColorScheme
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatryk.liftapp.core.extension.isLandscape
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.text.rememberDefaultMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.dimens.LandscapeDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.PortraitDimens

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

val chartStyle: ChartStyle
    @Composable get() = m3ChartStyle()

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

@Composable
fun LiftAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val dimens = if (isLandscape) LandscapeDimens else PortraitDimens
    val liftAppColorScheme = getLiftAppColorScheme(darkTheme)

    MaterialTheme(colorScheme = colorScheme, typography = LiftAppTypography, shapes = Shapes) {
        val markupProcessor = rememberDefaultMarkupProcessor()
        CompositionLocalProvider(
            LocalDimens provides dimens,
            LocalMarkupProcessor provides markupProcessor,
            LocalColorScheme provides liftAppColorScheme,
        ) {
            ProvideChartStyle(chartStyle = chartStyle, content = content)
        }
    }
}
