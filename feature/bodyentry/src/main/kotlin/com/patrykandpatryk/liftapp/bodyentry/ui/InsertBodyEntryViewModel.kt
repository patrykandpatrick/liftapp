package com.patrykandpatryk.liftapp.bodyentry.ui

import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class InsertBodyEntryViewModel @Inject constructor(
    private val stateHandler: ScreenStateHandler<ScreenState, Intent, Event>,
) : ViewModel(stateHandler), ScreenStateHandler<ScreenState, Intent, Event> by stateHandler
