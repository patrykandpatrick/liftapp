package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.data.NewBodyMeasurementRouteData
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.newbodymeasuremententry.model.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NewBodyMeasurementEntryViewModel
@Inject
constructor(
    routeData: NewBodyMeasurementRouteData,
    stateFactory: NewBodyMeasurementState.Factory,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    val state: NewBodyMeasurementState =
        stateFactory.create(
            id = routeData.bodyMeasurementID,
            entryId = routeData.bodyMeasurementEntryID,
            coroutineScope = viewModelScope,
        )

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> popBackStack()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }
}
