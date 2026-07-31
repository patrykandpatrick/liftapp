package com.patrykandpatrick.liftapp.core.message

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Confirmations that outlive the screen that raised them.
 *
 * A screen reporting its own success can host the snackbar itself. One that finishes by navigating
 * away cannot: it is gone before the message could be read. These are collected at the root
 * instead, above the navigation host, so the snackbar survives the screen.
 *
 * Deliberately not the [com.patrykandpatrick.liftapp.core.logging.UiLogger] path, which several
 * screens already collect into snackbars of their own — routing confirmations through it would show
 * every message on those screens twice.
 */
@Singleton
class ConfirmationPublisher @Inject constructor() {

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)

    val messages: Flow<String> = _messages.asSharedFlow()

    suspend fun publish(message: String) {
        _messages.emit(message)
    }
}
