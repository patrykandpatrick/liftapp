package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.RoutineListRouteData
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.feature.routines.model.Action
import com.patrykandpatryk.liftapp.feature.routines.model.GetRoutineItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class RoutineListViewModel
@Inject
constructor(
    getRoutineItemsUseCase: GetRoutineItemsUseCase,
    private val routineListRouteData: RoutineListRouteData,
    private val navigationCommander: NavigationCommander,
    scope: CoroutineScope,
) : ViewModel(scope) {
    val state: StateFlow<Loadable<RoutineListState>> =
        getRoutineItemsUseCase()
            .map { routineItems ->
                RoutineListState(
                    routines = routineItems,
                    isPickingRoutine = routineListRouteData.isPickingRoutine,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            is Action.PopBackStack -> popBackStack()
            is Action.RoutineClicked -> onRoutineClicked(action.routineID)
            is Action.AddNewRoutine -> addNewRoutine()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun onRoutineClicked(routineID: Long) {
        viewModelScope.launch {
            if (routineListRouteData.isPickingRoutine) {
                navigationCommander.publishResult(routineListRouteData.resultKey, routineID)
                navigationCommander.popBackStack()
            } else {
                navigationCommander.navigateTo(Routes.Routine.details(routineID))
            }
        }
    }

    private fun addNewRoutine() {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Routine.new()) }
    }
}
