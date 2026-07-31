package com.patrykandpatrick.liftapp.core.android

import com.patrykandpatrick.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatrick.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatrick.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.theme.isDarkMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Resolves the theme preference against the system's own dark mode setting, which the activity
 * publishes here whenever the configuration changes.
 *
 * The resolved value is recomputed synchronously as each of the two inputs arrives, so a launch
 * that ends up in dark mode does not compose a light frame first. A theme that disagrees with the
 * system still cannot be known until the preference has been read once.
 */
@Singleton
class IsDarkModeHandler
@Inject
constructor(
    preferenceRepository: PreferenceRepository,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
) : IsDarkModeReceiver, IsDarkModePublisher {

    private val coroutineScope = CoroutineScope(dispatcher + SupervisorJob())

    private val systemDarkMode = MutableStateFlow(false)

    private val theme = MutableStateFlow(Theme.FollowSystem)

    private val isDarkMode = MutableStateFlow(false)

    init {
        coroutineScope.launch {
            preferenceRepository.theme.get().collect { theme ->
                this@IsDarkModeHandler.theme.value = theme
                resolve()
            }
        }
    }

    override fun invoke(darkMode: Boolean) {
        systemDarkMode.value = darkMode
        resolve()
    }

    override fun invoke(): StateFlow<Boolean> = isDarkMode

    @Synchronized
    private fun resolve() {
        isDarkMode.value = theme.value.isDarkMode(systemDarkMode.value)
    }
}
