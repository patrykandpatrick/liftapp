package com.patrykandpatryk.liftapp.bodyrecord.ui

import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class InsertBodyRecordViewModel @Inject constructor(
    private val stateHandler: ScreenStateHandler<ScreenState, Intent>,
) : ViewModel(stateHandler), ScreenStateHandler<ScreenState, Intent> by stateHandler
