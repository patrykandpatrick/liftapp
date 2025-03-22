package com.patrykandpatryk.liftapp.feature.more.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.feature.more.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class MoreViewModel @Inject constructor(private val navigationCommander: NavigationCommander) :
    ViewModel() {
    fun navigateTo(destination: Destination) {
        viewModelScope.launch { navigationCommander.navigateTo(destination.getRoute()) }
    }
}
