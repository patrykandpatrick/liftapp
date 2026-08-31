package com.patrykandpatrick.liftapp.feature.plan.configurator.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.plan.SetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.target.GenerationParameters
import com.patrykandpatrick.liftapp.domain.target.TrainingIntent
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.domain.validation.validNumberHigherThanZero
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.feature.plan.configurator.model.Action
import com.patrykandpatrick.liftapp.navigation.data.PlanConfiguratorRouteData
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
    getPlanUseCase: GetPlanUseCase,
    stringProvider: StringProvider,
    textFieldStateManager: TextFieldStateManager,
    private val navigationCommander: NavigationCommander,
    private val setActivePlanUseCase: SetActivePlanUseCase,
    coroutineScope: CoroutineScope,
) : ViewModel(coroutineScope) {
    private val dateFormat = DateTimeFormatter.ofPattern(stringProvider.dateWeekdayDayMonthYear)

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

    private val startingRir =
        textFieldStateManager.intTextField(
            initialValue = GenerationParameters.DEFAULT_STARTING_RIR.toString(),
            onValueChange = { refreshState() },
            veto = { it !in ExerciseSet.RIR_RANGE },
        )

    private var intent = TrainingIntent.Both

    private var rebuildTargets = false

    private val refreshState = MutableSharedFlow<Unit>(replay = 1)

    val screenState: StateFlow<Loadable<ScreenState>> =
        getPlanUseCase
            .getPlan(routeData.planID)
            .combine(refreshState.onStart { emit(Unit) }) { plan, _ -> getScreenState(plan) }
            .toLoadableStateFlow(viewModelScope)

    private fun getScreenState(plan: Plan): ScreenState {
        val daysInCycle = plan.items.size * cycleCount.value
        return ScreenState(
            plan = plan,
            startDate = startDate,
            cycleCount = cycleCount,
            intent = intent,
            startingRir = startingRir,
            rebuildTargets = rebuildTargets,
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
            is Action.SetIntent -> setIntent(action.intent)
            is Action.SetRebuildTargets -> setRebuildTargets(action.rebuild)
            is Action.Save -> save(action.state.plan)
        }
    }

    private fun setIntent(intent: TrainingIntent) {
        this.intent = intent
        refreshState()
    }

    private fun setRebuildTargets(rebuild: Boolean) {
        rebuildTargets = rebuild
        refreshState()
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun save(plan: Plan) {
        viewModelScope.launch {
            setActivePlanUseCase(
                plan = plan,
                startDate = startDate.value,
                parameters =
                    GenerationParameters(
                        intent = intent,
                        cycleCount = cycleCount.value,
                        startingRir = startingRir.value,
                    ),
                rebuildTargets = rebuildTargets,
            )
            navigationCommander.popBackStack()
        }
    }

    private companion object {
        const val MAX_CYCLE_COUNT = 999
    }
}
