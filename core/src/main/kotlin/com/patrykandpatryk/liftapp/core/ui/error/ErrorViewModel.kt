package com.patrykandpatryk.liftapp.core.ui.error

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ErrorViewModel @Inject constructor(private val navigationCommander: NavigationCommander) :
    ViewModel() {

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> popBackStack()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }
}
