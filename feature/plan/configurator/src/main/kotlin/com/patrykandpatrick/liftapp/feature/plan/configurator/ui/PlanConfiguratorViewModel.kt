package com.patrykandpatrick.liftapp.feature.plan.configurator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.feature.plan.configurator.model.Action
import com.patrykandpatrick.liftapp.navigation.data.PlanConfiguratorRouteData
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.plan.GetPlanContract
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.plan.SetActivePlanUseCase
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.validation.validNumberHigherThanZero
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class PlanConfiguratorViewModel
@Inject
constructor(
    private val routeData: PlanConfiguratorRouteData,
    getPlanContract: GetPlanContract,
    stringProvider: StringProvider,
    textFieldStateManager: TextFieldStateManager,
    private val navigationCommander: NavigationCommander,
    private val setActivePlanUseCase: SetActivePlanUseCase,
    coroutineScope: CoroutineScope,
) : ViewModel(coroutineScope) {
    private val dateFormat = DateTimeFormatter.ofPattern(stringProvider.dateFormatEdit)

    private val startDate =
        textFieldStateManager.localDateField(
            formatter = dateFormat,
            onValueChange = { refreshState() },
        )

    private val cycleCount =
        textFieldStateManager.intTextField(
            initialValue = Constants.TrainingPlan.DEFAULT_CYCLE_COUNT.toString(),
            validators = { validNumberHigherThanZero() },
            onValueChange = { refreshState() },
            veto = { it > MAX_CYCLE_COUNT },
        )

    private val refreshState = MutableSharedFlow<Unit>(replay = 1)

    val screenState: StateFlow<Loadable<ScreenState>> =
        getPlanContract
            .getPlan(routeData.planID)
            .combine(refreshState.onStart { emit(Unit) }) { plan, _ -> getScreenState(plan) }
            .toLoadableStateFlow(viewModelScope)

    private fun getScreenState(plan: Plan): ScreenState {
        val daysInCycle = plan.items.size * cycleCount.value
        return ScreenState(
            plan = plan,
            startDate = startDate,
            cycleCount = cycleCount,
            endDate = startDate.value.plusDays(daysInCycle.toLong()),
            lengthWeeks = daysInCycle / DayOfWeek.entries.size,
            lengthRemainingDays = daysInCycle % DayOfWeek.entries.size,
        )
    }

    private fun refreshState() {
        viewModelScope.launch { refreshState.emit(Unit) }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> popBackStack()
            is Action.Save -> save(action.state.plan)
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun save(plan: Plan) {
        viewModelScope.launch {
            setActivePlanUseCase(
                plan = plan,
                startDate = startDate.value,
                cycleCount = cycleCount.value,
            )
            navigationCommander.popBackStack()
        }
    }

    private companion object {
        const val MAX_CYCLE_COUNT = 999
    }
}
