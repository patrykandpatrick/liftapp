package com.patrykandpatrick.liftapp.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.patrykandpatrick.liftapp.core.deeplink.DeepLink
import com.patrykandpatrick.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatrick.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.navigation.Routes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var darkModePublisher: IsDarkModePublisher

    @Inject lateinit var darkModeReceiver: IsDarkModeReceiver

    @Inject lateinit var navigationCommander: NavigationCommander

    private var edgeToEdgeDarkMode: Boolean? = null

    /** The backup another app asked LiftApp to open, until the composition has navigated to it. */
    private var backupToOpen by mutableStateOf<String?>(null)

    /** A deep link delivered to this existing single-top activity. */
    private var deepLinkToOpen by mutableStateOf<Intent?>(null)

    private var backupIntentHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateDarkMode(resources.configuration.isDarkMode)
        enableEdgeToEdge(darkModeReceiver().value)
        backupIntentHandled = savedInstanceState?.getBoolean(BACKUP_INTENT_HANDLED) == true
        if (!backupIntentHandled) takeBackupToOpen(intent)

        setContent {
            val darkMode by darkModeReceiver().collectAsState()

            LaunchedEffect(darkMode) { enableEdgeToEdge(darkMode) }

            Root(
                darkTheme = darkMode,
                navigationCommander = navigationCommander,
                deepLinkIntent = deepLinkToOpen,
                onDeepLinkHandled = { deepLinkToOpen = null },
            )

            // Composed after `Root`, so the collector behind `navigationCommander` is already
            // subscribed by the time this runs. Commands are not replayed to late subscribers.
            LaunchedEffect(backupToOpen) {
                val location = backupToOpen ?: return@LaunchedEffect
                backupToOpen = null
                navigationCommander.navigateTo(Routes.Backup.restore(location))
                backupIntentHandled = true
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.data?.scheme == DeepLink.SCHEME) {
            deepLinkToOpen = intent
        } else {
            takeBackupToOpen(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(BACKUP_INTENT_HANDLED, backupIntentHandled)
        super.onSaveInstanceState(outState)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateDarkMode(newConfig.isDarkMode)
    }

    /**
     * Picks up the backup file another app opened with LiftApp.
     *
     * The document is not one of the app's own deep links — it is whatever URI the file manager or
     * mail client handed over — so the route is built from [Intent.getData] rather than matched.
     * Anything on the `liftapp` scheme is handled separately by the navigation graph. The intent's
     * data is cleared, or a configuration change would open the screen a second time.
     */
    private fun takeBackupToOpen(intent: Intent) {
        if (intent.action != Intent.ACTION_VIEW) return
        val uri = intent.data ?: return
        if (uri.scheme == DeepLink.SCHEME) return
        intent.data = null
        backupToOpen = uri.toString()
    }

    private fun updateDarkMode(darkMode: Boolean) {
        darkModePublisher(darkMode)
    }

    /**
     * Tells the system bars which theme won, since the theme preference can disagree with the
     * configuration `enableEdgeToEdge` would otherwise read.
     *
     * Applying the same value twice is skipped rather than repeated, because doing so also resets
     * the bar appearance a screen has set for itself. Without that, the call the first composition
     * makes would land after the screen's own and undo it, which a deep link straight into
     * [com.patrykandpatrick.liftapp.feature.workout.ui.WorkoutScreen] and its dark header shows.
     */
    private fun enableEdgeToEdge(darkMode: Boolean) {
        if (edgeToEdgeDarkMode == darkMode) return
        edgeToEdgeDarkMode = darkMode
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkMode },
            navigationBarStyle = SystemBarStyle.auto(LIGHT_SCRIM, DARK_SCRIM) { darkMode },
        )
    }

    private companion object {
        const val BACKUP_INTENT_HANDLED = "backup_intent_handled"

        /**
         * The scrims `enableEdgeToEdge` gives the navigation bar by default, which it keeps
         * private. Only API 28 and below use them, where the system cannot enforce the contrast
         * itself.
         */
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}
