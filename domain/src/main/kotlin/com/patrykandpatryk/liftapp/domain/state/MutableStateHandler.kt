package com.patrykandpatryk.liftapp.domain.state

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

abstract class MutableStateHandler<ScreenState, Intent, Event>(
    dispatcher: CoroutineDispatcher,
    exceptionHandler: CoroutineExceptionHandler,
) : StateHandler<ScreenState, Intent, Event> {

    protected val coroutineScope = CoroutineScope(exceptionHandler + dispatcher + SupervisorJob())

    private val eventChannel = Channel<Event>()

    abstract override val state: MutableStateFlow<ScreenState>

    override val events: Flow<Event> = eventChannel.receiveAsFlow()

    protected fun updateState(block: ScreenState.() -> ScreenState) {
        state.update(block)
    }

    protected suspend fun sendEvent(event: Event) {
        eventChannel.send(event)
    }

    override fun close() {
        coroutineScope.cancel()
    }
}
