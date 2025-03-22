package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.feature.dashboard.model.Action
import com.patrykandpatryk.liftapp.feature.dashboard.model.GetDashboardStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel
@Inject
constructor(
    getDashboardStateUseCase: GetDashboardStateUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {
    val state =
        getDashboardStateUseCase().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun onAction(action: Action) {
        when (action) {
            is Action.NewWorkout -> newWorkout(action.routineID)
            is Action.OpenWorkout -> openWorkout(action.workoutID)
        }
    }

    private fun newWorkout(routineID: Long) {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Workout.new(routineID)) }
    }

    private fun openWorkout(workoutID: Long) {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Workout.edit(workoutID)) }
    }
}
