package com.patrykandpatrick.liftapp.plan.list.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.data.PlanListRouteData
import com.patrykandpatrick.liftapp.plan.list.model.Action
import com.patrykandpatrick.liftapp.plan.list.model.ScreenState
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.plan.GetAllPlansUseCase
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
            Action.SaveSelection -> saveSelection()
            is Action.CheckPlan -> checkedPlanID(action.id)
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun checkedPlanID(id: Long) {
        savedStateHandle[ID_KEY] = id
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
