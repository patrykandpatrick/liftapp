package com.patrykandpatryk.liftapp.domain.state

import java.io.Closeable
import kotlinx.coroutines.flow.StateFlow

interface ScreenStateHandler<ScreenState, Intent> : Closeable {

    val state: StateFlow<ScreenState>

    fun handleIntent(intent: Intent)

    operator fun invoke(intent: Intent) = handleIntent(intent)
}
