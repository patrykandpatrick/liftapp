package com.patrykandpatrick.liftapp.plan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.plan.model.GetPlanStateUseCase
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PlanViewModel
@Inject
constructor(
    getPlanStateUseCase: GetPlanStateUseCase,
    scope: CoroutineScope,
    private val navigationCommander: NavigationCommander,
) : ViewModel(scope) {

    val state: StateFlow<Loadable<PlanState>> =
        getPlanStateUseCase().toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            Action.ChooseExistingPlan -> chooseExistingPlan()
            Action.CreateNewPlan -> createNewPlan()
        }
    }

    private fun chooseExistingPlan() {
        viewModelScope.launch { TODO() }
    }

    private fun createNewPlan() {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Plan.new()) }
    }
}
