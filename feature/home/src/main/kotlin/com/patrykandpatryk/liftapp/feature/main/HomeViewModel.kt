package com.patrykandpatryk.liftapp.feature.main

import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val logger: UiLogger,
) : ViewModel(), LogPublisher by logger
