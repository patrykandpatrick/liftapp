package com.patrykandpatryk.liftapp.core.android

import com.patrykandpatryk.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsDarkModeHandler @Inject constructor() : IsDarkModeReceiver, IsDarkModePublisher {

    private val isDarkMode: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override fun invoke(darkMode: Boolean) {
        isDarkMode.value = darkMode
    }

    override fun invoke(): StateFlow<Boolean> = isDarkMode
}
