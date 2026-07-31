package com.patrykandpatrick.liftapp.feature.newroutine.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.serialization.saved
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.exception.RoutineNotFoundException
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.Routine
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithItems
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.invoke
import com.patrykandpatrick.liftapp.domain.validation.nonEmpty
import com.patrykandpatrick.liftapp.feature.newroutine.model.Action
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

@HiltViewModel
class NewRoutineViewModel
@Inject
constructor(
    viewModelScope: CoroutineScope,
    private val getRoutineWithItemsUseCase: GetRoutineWithItemsUseCase,
    textFieldStateManager: TextFieldStateManager,
    private val routeData: NewRoutineRouteData,
    private val upsertRoutine: UpsertRoutineUseCase,
    private val navigationCommander: NavigationCommander,
    savedStateHandle: SavedStateHandle,
) : ViewModel(viewModelScope) {
    private val name = textFieldStateManager.stringTextField(validators = { nonEmpty() })

    /** Guards against the stored name overwriting the one the user is editing. */
    private var isNameLoaded by savedStateHandle.saved { false }

    val state =
        getRoutine()
            .map { routine ->
                if (routine != null && !isNameLoaded) {
                    isNameLoaded = true
                    name.updateText(routine.name)
                }
                NewRoutineState(name = name, isEdit = routine != null)
            }
            .toLoadableStateFlow(viewModelScope)

    internal fun onAction(action: Action) {
        when (action) {
            is Action.SaveRoutine -> save()
            is Action.PopBackStack -> popBackStack()
        }
    }

    private fun getRoutine(): Flow<RoutineWithItems?> =
        if (routeData.routineID == ID_NOT_SET) {
            flowOf(null)
        } else {
            getRoutineWithItemsUseCase(routeData.routineID).transform { routine ->
                if (routine == null) {
                    throw RoutineNotFoundException(routeData.routineID)
                } else {
                    emit(routine)
                }
            }
        }

    private fun save() {
        name.updateErrorMessages()
        if (name.hasError) return

        viewModelScope.launch {
            val routineID = upsertRoutine(Routine(name = name.value, id = routeData.routineID))
            navigationCommander.popBackStack()
            if (routeData.routineID == ID_NOT_SET) {
                navigationCommander.navigateTo(Routes.Routine.details(routineID))
            }
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }
}
