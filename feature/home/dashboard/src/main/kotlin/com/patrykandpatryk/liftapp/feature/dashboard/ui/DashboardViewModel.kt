package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.feature.dashboard.model.GetDashboardStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DashboardViewModel @Inject constructor(getDashboardStateUseCase: GetDashboardStateUseCase) :
    ViewModel() {
    val state =
        getDashboardStateUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
}
