package com.patrykandpatrick.liftapp.core.logging

import kotlinx.coroutines.flow.SharedFlow

interface LogPublisher {

    val messages: SharedFlow<UiMessage>
}
