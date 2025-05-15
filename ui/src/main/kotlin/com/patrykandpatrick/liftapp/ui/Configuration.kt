package com.patrykandpatrick.liftapp.ui

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

val isLandscape: Boolean
    @Composable
    get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

val isPortrait: Boolean
    @Composable get() = isLandscape.not()

val Context.isDarkMode: Boolean
    get() = resources.configuration.isDarkMode

val Configuration.isDarkMode: Boolean
    get() = uiMode and UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
