package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

@HiltViewModel
class BodyViewModel @Inject constructor(
    getBodyItems: GetBodyItemsUseCase,
    exceptionHandler: CoroutineExceptionHandler,
) : ViewModel() {

    val bodyItems = getBodyItems()
        .stateIn(
            scope = viewModelScope + exceptionHandler,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )
}
