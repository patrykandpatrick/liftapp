package com.patrykandpatrick.liftapp.plan.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.plan.model.GetPlanStateUseCase
import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.plan.ActivePlan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class PlanViewModel
@Inject
constructor(
    getPlanStateUseCase: GetPlanStateUseCase,
    scope: CoroutineScope,
    private val navigationCommander: NavigationCommander,
    @PreferenceQualifier.ActivePlan private val activePlan: Preference<ActivePlan?>,
) : ViewModel(scope) {

    val state: StateFlow<Loadable<PlanState>> =
        getPlanStateUseCase().toLoadableStateFlow(viewModelScope)

    init {
        observeTrainingPlanSelection()
    }

    fun onAction(action: Action) {
        when (action) {
            Action.ChooseExistingPlan -> chooseExistingPlan()
            Action.CreateNewPlan -> createNewPlan()
        }
    }

    private fun chooseExistingPlan() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Plan.select(TRAINING_PLAN_KEY))
        }
    }

    private fun createNewPlan() {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Plan.new()) }
    }

    private fun observeTrainingPlanSelection() {
        navigationCommander
            .getResults<Long>(TRAINING_PLAN_KEY)
            .onEach { id ->
                // TODO navigate to plan start screen
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val TRAINING_PLAN_KEY = "key_training_plan"
    }
}
