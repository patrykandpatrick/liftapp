package com.patrykandpatryk.liftapp.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.patrykandpatryk.liftapp.core.extension.isDarkMode
import com.patrykandpatryk.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var darkModePublisher: IsDarkModePublisher

    @Inject lateinit var darkModeReceiver: IsDarkModeReceiver

    @Inject lateinit var navigationCommander: NavigationCommander

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateDarkMode(resources.configuration.isDarkMode)
        enableEdgeToEdge()

        setContent {
            val darkMode by darkModeReceiver().collectAsState()
            Root(darkTheme = darkMode, navigationCommander = navigationCommander)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDarkMode(newConfig.isDarkMode)
        enableEdgeToEdge()
    }

    private fun updateDarkMode(darkMode: Boolean) {
        darkModePublisher(darkMode)
    }
}
