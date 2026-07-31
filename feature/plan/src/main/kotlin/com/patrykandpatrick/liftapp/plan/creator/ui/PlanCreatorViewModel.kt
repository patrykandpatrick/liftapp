package com.patrykandpatrick.liftapp.plan.creator.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.serialization.saved
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.extension.update
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.plan.DeletePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.PlanCreatorRouteData
import com.patrykandpatrick.liftapp.plan.creator.model.Action
import com.patrykandpatrick.liftapp.plan.creator.model.UpsertPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class PlanCreatorViewModel
@Inject
constructor(
    private val routeData: PlanCreatorRouteData,
    private val getPlanUseCase: GetPlanUseCase,
    private val upsertPlanUseCase: UpsertPlanUseCase,
    private val deletePlanUseCase: DeletePlanUseCase,
    private val getRoutineWithExercisesUseCase: GetRoutineWithExercisesUseCase,
    private val textFieldStateManager: TextFieldStateManager,
    private val savedStateHandle: SavedStateHandle,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    private var pickedRoutineIndex: Int by savedStateHandle.saved { -1 }

    private var planItems: List<ScreenState.Item>
        get() = savedStateHandle[KEY_ITEMS] ?: emptyList()
        set(value) {
            savedStateHandle[KEY_ITEMS] = value
        }

    private val name = textFieldStateManager.stringTextField()
    private val description = textFieldStateManager.stringTextField()

    private val planOrNull: Flow<Plan?> = flow {
        if (routeData.planID == ID_NOT_SET) {
            emit(null)
        } else {
            val plan = getPlanUseCase.getPlan(routeData.planID)
            emitAll(plan)
        }
    }
        .onEach { plan ->
            if (!savedStateHandle.contains(KEY_ITEMS)) {
                // The placeholder is the row that adds a day, and both add actions insert
                // ahead of it, so it has to close the list whether the plan is new or not.
                planItems =
                    plan?.items.orEmpty().toNewScreenStateItems() + ScreenState.Item.PlaceholderItem
            }
        }

    val state: StateFlow<Loadable<ScreenState>> = run {
        planOrNull
            .flatMapLatest { plan ->
                savedStateHandle.getStateFlow(KEY_ITEMS, emptyList<ScreenState.Item>()).map { items
                    ->
                    plan to items
                }
            }
            .map { (plan, items) ->
                if (name.text.isBlank()) {
                    name.updateText(plan?.name.orEmpty())
                }

                if (description.text.isBlank()) {
                    description.updateText(plan?.description.orEmpty())
                }
                ScreenState(
                    id = plan?.id ?: ID_NOT_SET,
                    name = name,
                    description = description,
                    items = items,
                )
            }
            .toLoadableStateFlow(viewModelScope)
    }

    init {
        observeRoutineSelection()
    }

    private fun List<Plan.Item>.toNewScreenStateItems(): List<ScreenState.Item> = map { item ->
        when (item) {
            is Plan.Item.Routine -> ScreenState.Item.RoutineItem(item.routine)
            is Plan.Item.Rest -> ScreenState.Item.RestItem()
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.PopBackStack -> popBackStack()
            is Action.OnRoutineClick -> openRoutine(action.routineID)
            is Action.AddRestDay -> updatePlanItems { add(lastIndex, ScreenState.Item.RestItem()) }
            is Action.AddRoutine -> setOrAddRoutine()
            is Action.RemoveItem -> updatePlanItems { removeAt(action.index) }
            is Action.DeletePlan -> deletePlan(action.id)
            is Action.Save -> savePlan(action.state)
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun openRoutine(routineID: Long) {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Routine.details(routineID)) }
    }

    private fun deletePlan(id: Long) {
        viewModelScope.launch {
            deletePlanUseCase(id)
            navigationCommander.popBackStack()
        }
    }

    private fun setOrAddRoutine() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Routine.pickRoutine(PICK_ROUTINE_REQUEST_KEY))
        }
    }

    private fun updatePlanItems(update: MutableList<ScreenState.Item>.() -> Unit) {
        savedStateHandle.update<List<ScreenState.Item>>(KEY_ITEMS) { items ->
            checkNotNull(items).toMutableList().apply(update)
        }
    }

    private fun observeRoutineSelection() {
        navigationCommander
            .getResults<Long>(PICK_ROUTINE_REQUEST_KEY)
            .onEach { routineID ->
                val routine =
                    checkNotNull(
                        getRoutineWithExercisesUseCase.getRoutineWithExercises(routineID).first()
                    ) {
                        "Routine with ID $routineID was picked, but could not be found in the database"
                    }
                updatePlanItems {
                    if (pickedRoutineIndex == -1) {
                        add(lastIndex, ScreenState.Item.RoutineItem(routine))
                    } else {
                        set(pickedRoutineIndex, ScreenState.Item.RoutineItem(routine))
                    }
                }
                pickedRoutineIndex = -1
            }
            .launchIn(viewModelScope)
    }

    private fun savePlan(state: ScreenState) {
        if (!state.canSave) return

        viewModelScope.launch {
            upsertPlanUseCase(state)
            navigationCommander.popBackStack()
        }
    }

    companion object {
        private const val KEY_ITEMS = "items"
        private const val PICK_ROUTINE_REQUEST_KEY = "pick_routine_id"
    }
}
