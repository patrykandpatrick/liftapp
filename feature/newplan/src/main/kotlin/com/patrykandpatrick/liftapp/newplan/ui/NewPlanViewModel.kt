package com.patrykandpatrick.liftapp.newplan.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.serialization.saved
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.NewPlanRouteData
import com.patrykandpatrick.liftapp.newplan.model.Action
import com.patrykandpatrick.liftapp.newplan.model.UpsertPlanUseCase
import com.patrykandpatryk.liftapp.core.extension.update
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.plan.GetPlanContract
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExercisesContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NewPlanViewModel
@Inject
constructor(
    private val routeData: NewPlanRouteData,
    private val getPlanContract: GetPlanContract,
    private val upsertPlanUseCase: UpsertPlanUseCase,
    private val getRoutineWithExercisesContract: GetRoutineWithExercisesContract,
    private val textFieldStateManager: TextFieldStateManager,
    private val savedStateHandle: SavedStateHandle,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    private var pickedRoutineIndex: Int by savedStateHandle.saved { -1 }

    private var planItems: List<NewPlanState.Item>
        get() = savedStateHandle[KEY_ITEMS] ?: emptyList()
        set(value) {
            savedStateHandle[KEY_ITEMS] = value
        }

    private val error = MutableStateFlow<NewPlanState.Error?>(null)

    private val planOrNull: Flow<Plan?> =
        flow {
                if (routeData.planID == ID_NOT_SET) {
                    emit(null)
                } else {
                    val plan = getPlanContract.getPlan(routeData.planID)
                    emitAll(plan)
                }
            }
            .onEach { plan ->
                if (!savedStateHandle.contains(KEY_ITEMS)) {
                    planItems =
                        plan?.items?.toNewPlanStateItems()
                            ?: listOf(NewPlanState.Item.PlaceholderItem)
                }
            }

    val state: StateFlow<Loadable<NewPlanState>> = run {
        planOrNull
            .flatMapLatest { plan ->
                savedStateHandle.getStateFlow(KEY_ITEMS, emptyList<NewPlanState.Item>()).map { items
                    ->
                    plan to items
                }
            }
            .combine(error) { (plan, items), error ->
                NewPlanState(
                    id = plan?.id ?: ID_NOT_SET,
                    name =
                        textFieldStateManager.stringTextField(initialValue = plan?.name.orEmpty()),
                    description =
                        textFieldStateManager.stringTextField(
                            initialValue = plan?.description.orEmpty()
                        ),
                    items = items,
                    error = error,
                )
            }
            .toLoadableStateFlow(viewModelScope)
    }

    init {
        observeRoutineSelection()
    }

    private fun List<Plan.Item>.toNewPlanStateItems(): List<NewPlanState.Item> = map { item ->
        when (item) {
            is Plan.Item.RoutineItem -> NewPlanState.Item.RoutineItem(item.routine)
            is Plan.Item.RestItem -> NewPlanState.Item.RestItem()
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.PopBackStack -> popBackStack()
            is Action.OnPlanElementClick -> TODO("Support updating plan items")
            is Action.AddRestDay -> updatePlanItems { add(lastIndex, NewPlanState.Item.RestItem()) }
            is Action.AddRoutine -> setOrAddRoutine()
            is Action.RemoveItem -> updatePlanItems { removeAt(action.index) }
            is Action.Save -> savePlan(action.state)
            is Action.ClearError -> clearError()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun setOrAddRoutine() {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Routine.pickRoutine(PICK_ROUTINE_REQUEST_KEY))
        }
    }

    private fun updatePlanItems(update: MutableList<NewPlanState.Item>.() -> Unit) {
        savedStateHandle.update<List<NewPlanState.Item>>(KEY_ITEMS) { items ->
            checkNotNull(items).toMutableList().apply(update)
        }
    }

    private fun observeRoutineSelection() {
        navigationCommander
            .getResults<Long>(PICK_ROUTINE_REQUEST_KEY)
            .onEach { routineID ->
                val routine =
                    checkNotNull(
                        getRoutineWithExercisesContract.getRoutineWithExercises(routineID).first()
                    ) {
                        "Routine with ID $routineID was picked, but could not be found in the database"
                    }
                updatePlanItems {
                    if (pickedRoutineIndex == -1) {
                        add(lastIndex, NewPlanState.Item.RoutineItem(routine))
                    } else {
                        set(pickedRoutineIndex, NewPlanState.Item.RoutineItem(routine))
                    }
                }
                pickedRoutineIndex = -1
            }
            .launchIn(viewModelScope)
    }

    private fun savePlan(state: NewPlanState) {
        viewModelScope.launch {
            if (state.items.none { it is NewPlanState.Item.RoutineItem }) {
                error.emit(NewPlanState.Error.NoRoutines)
                return@launch
            }

            upsertPlanUseCase(state)
            navigationCommander.popBackStack()
        }
    }

    private fun clearError() {
        error.update { null }
    }

    companion object {
        private const val KEY_ITEMS = "items"
        private const val PICK_ROUTINE_REQUEST_KEY = "pick_routine_id"
    }
}
