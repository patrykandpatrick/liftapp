package com.patrykandpatrick.liftapp.plan.list.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.plan.GetAllPlansUseCase
import com.patrykandpatrick.liftapp.domain.plan.invoke
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.PlanListRouteData
import com.patrykandpatrick.liftapp.plan.list.model.Action
import com.patrykandpatrick.liftapp.plan.list.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class PlanListViewModel
@Inject
constructor(
    getAllPlansUseCase: GetAllPlansUseCase,
    private val routeData: PlanListRouteData,
    private val savedStateHandle: SavedStateHandle,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    val screenState =
        combine(getAllPlansUseCase(), savedStateHandle.getStateFlow(ID_KEY, ID_NOT_SET)) {
                plans,
                checkedID ->
                ScreenState.create(
                    plans = plans,
                    checkedID = checkedID,
                    isPickingTrainingPlan = routeData.isPickingTrainingPlan,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> popBackStack()
            Action.AddNewPlan -> addNewPlan()
            Action.SaveSelection -> saveSelection()
            is Action.OnPlanClick -> onPlanClick(action.id)
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun addNewPlan() {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Plan.new()) }
    }

    /**
     * The same tap means different things in the two modes this screen has: it selects a plan when
     * one is being picked, and opens it for editing when the list is browsed from `More`.
     */
    private fun onPlanClick(id: Long) {
        if (routeData.isPickingTrainingPlan) {
            savedStateHandle[ID_KEY] = id
        } else {
            viewModelScope.launch { navigationCommander.navigateTo(Routes.Plan.edit(id)) }
        }
    }

    private fun saveSelection() {
        val planID = savedStateHandle.get<Long>(ID_KEY) ?: return
        viewModelScope.launch {
            navigationCommander.publishResult(routeData.resultKey, planID)
            navigationCommander.popBackStack()
        }
    }

    companion object {
        private const val ID_KEY = "key_id"
    }
}
