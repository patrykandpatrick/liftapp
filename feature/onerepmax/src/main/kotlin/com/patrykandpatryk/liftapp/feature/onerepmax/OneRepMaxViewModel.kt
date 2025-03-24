package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.feature.onerepmax.model.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OneRepMaxViewModel
@Inject
constructor(
    oneRepMaxStateFactory: OneRepMaxState.Factory,
    savedStateHandle: SavedStateHandle,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {
    val state = oneRepMaxStateFactory.create(viewModelScope, savedStateHandle)

    fun onAction(action: Action) {
        when (action) {
            is Action.PopBackStack -> popBackStack()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }
}
