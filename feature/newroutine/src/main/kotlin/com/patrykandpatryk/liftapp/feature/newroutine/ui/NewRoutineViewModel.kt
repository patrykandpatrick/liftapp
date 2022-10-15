package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.newroutine.domain.Event
import com.patrykandpatryk.liftapp.feature.newroutine.domain.Intent
import com.patrykandpatryk.liftapp.feature.newroutine.domain.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewRoutineViewModel @Inject constructor(
    private val logger: UiLogger,
    stateHandler: ScreenStateHandler<ScreenState, Intent, Event>,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Event> by stateHandler, LogPublisher by logger
