package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OneRepMaxViewModel @Inject constructor(
    oneRepMaxStateFactory: OneRepMaxState.Factory,
) : ViewModel() {
    val state = oneRepMaxStateFactory.create(coroutineScope = viewModelScope)
}
