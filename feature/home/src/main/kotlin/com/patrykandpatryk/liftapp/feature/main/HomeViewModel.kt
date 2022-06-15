package com.patrykandpatryk.liftapp.feature.main

import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.core.logging.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logger: UiLogger,
) : ViewModel() {

    val messages: Flow<UiMessage> = logger.messages
}
