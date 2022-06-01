package pl.patrykgoworowski.mintlift.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Orange30,
    onPrimary = Color.Black,
    secondary = Orange50,
    onSecondary = Color.Black,
    tertiary = Orange70,
    onTertiary = Color.White,
    background = Black70,
    surface = Black90,
)

private val LightColorScheme = lightColorScheme(
    primary = Orange50,
    onPrimary = Color.Black,
    secondary = Orange70,
    onSecondary = Color.White,
    tertiary = Orange70,
    onTertiary = White90,
    background = White70,
    surface = Color.White,
)

@Composable
fun LiftAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
