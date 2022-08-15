package com.patrykandpatryk.liftapp.domain.state

import java.io.Closeable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ScreenStateHandler<ScreenState, Intent, Event> : Closeable {

    val state: StateFlow<ScreenState>

    val events: Flow<Event>

    fun handleIntent(intent: Intent)

    operator fun invoke(intent: Intent) = handleIntent(intent)
}
