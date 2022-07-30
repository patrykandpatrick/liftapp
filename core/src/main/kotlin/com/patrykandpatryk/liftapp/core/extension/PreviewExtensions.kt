package com.patrykandpatryk.liftapp.core.extension

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun ProvideLandscapeMode(content: @Composable () -> Unit) {
    val configuration = Configuration(LocalConfiguration.current)
    configuration.orientation = Configuration.ORIENTATION_LANDSCAPE
    CompositionLocalProvider(LocalConfiguration provides configuration, content = content)
}
