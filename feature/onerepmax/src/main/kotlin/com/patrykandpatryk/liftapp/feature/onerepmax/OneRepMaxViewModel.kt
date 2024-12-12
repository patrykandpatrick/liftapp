package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OneRepMaxViewModel
@Inject
constructor(oneRepMaxStateFactory: OneRepMaxState.Factory, savedStateHandle: SavedStateHandle) :
    ViewModel() {
    val state = oneRepMaxStateFactory.create(viewModelScope, savedStateHandle)
}
