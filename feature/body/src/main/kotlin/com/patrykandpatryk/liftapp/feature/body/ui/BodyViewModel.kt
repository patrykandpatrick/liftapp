package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class BodyViewModel @Inject constructor(
    getBodyItems: GetBodyItemsUseCase,
) : ViewModel() {

    val bodyItems = getBodyItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )
}
