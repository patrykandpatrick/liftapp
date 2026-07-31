package com.patrykandpatrick.liftapp.ui

import androidx.lifecycle.ViewModel
import com.patrykandpatrick.liftapp.core.logging.LogPublisher
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.domain.format.Formatter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(logger: UiLogger, val formatter: Formatter) :
    ViewModel(), LogPublisher by logger
