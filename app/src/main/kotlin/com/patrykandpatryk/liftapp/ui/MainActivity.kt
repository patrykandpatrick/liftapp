package com.patrykandpatryk.liftapp.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.patrykandpatryk.liftapp.core.extension.isDarkMode
import com.patrykandpatryk.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var darkModePublisher: IsDarkModePublisher

    @Inject
    lateinit var darkModeReceiver: IsDarkModeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        updateDarkMode(resources.configuration.isDarkMode)

        setContent {
            val darkMode by darkModeReceiver().collectAsState()
            Root(darkTheme = darkMode)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDarkMode(newConfig.isDarkMode)
    }

    private fun updateDarkMode(darkMode: Boolean) {
        darkModePublisher(darkMode)
    }
}
