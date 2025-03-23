package com.patrykandpatryk.liftapp.ui

import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(logger: UiLogger) : ViewModel(), LogPublisher by logger
