package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.workout.GetActiveWorkoutsUseCase
import com.patrykandpatryk.liftapp.domain.workout.GetPastWorkoutsUseCase
import com.patrykandpatryk.liftapp.feature.dashboard.model.Action
import com.patrykandpatryk.liftapp.feature.dashboard.model.DashboardState
import com.patrykandpatryk.liftapp.feature.dashboard.model.GetPlanScheduleItemUseCase
import com.patrykandpatryk.liftapp.feature.dashboard.model.PlanScheduleItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel
@Inject
constructor(
    getActiveWorkoutsUseCase: GetActiveWorkoutsUseCase,
    getPastWorkoutsUseCase: GetPastWorkoutsUseCase,
    coroutineScope: CoroutineScope,
    private val navigationCommander: NavigationCommander,
    savedStateHandle: SavedStateHandle,
    private val getPlanScheduleItemUseCase: GetPlanScheduleItemUseCase,
) : ViewModel(coroutineScope) {

    private val selectedDate =
        savedStateHandle.getMutableStateFlow("selected_date", LocalDate.now())

    val state =
        combine(
                getActiveWorkoutsUseCase(),
                getPastWorkoutsUseCase(),
                selectedDate,
                selectedDate.flatMapLatest { date -> getPlanScheduleItemUseCase(date) },
            ) { activeWorkouts, pastWorkouts, selectedDate, planItem ->
                val planItemWorkout = (planItem as? PlanScheduleItem.Routine)?.workout
                DashboardState(
                    dayItems = getWeekDays(selectedDate),
                    selectedDate = selectedDate,
                    activeWorkouts = activeWorkouts.filter { it.id != planItemWorkout?.id },
                    pastWorkouts = pastWorkouts.filter { it.id != planItemWorkout?.id },
                    planScheduleItem = planItem,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            is Action.NewWorkout -> newWorkout(action.routineID)
            is Action.GoToWorkout -> goToWorkout(action.workoutID)
            is Action.GoToRoutine -> goToRoutine(action.routineID)
            is Action.SelectDate -> selectedDate.value = action.date
            is Action.Navigate -> navigate(action.route)
        }
    }

    private fun newWorkout(routineID: Long) {
        navigate(Routes.Workout.new(routineID))
    }

    private fun goToWorkout(workoutID: Long) {
        navigate(Routes.Workout.edit(workoutID))
    }

    private fun goToRoutine(routineID: Long) {
        navigate(Routes.Routine.details(routineID))
    }

    private fun navigate(route: Any) {
        viewModelScope.launch { navigationCommander.navigateTo(route) }
    }

    companion object {
        internal fun getWeekDays(selectedDate: LocalDate): List<DashboardState.DayItem> {
            val today = LocalDate.now()
            val startDate = today.run { minusDays(dayOfWeek.ordinal.toLong()) }

            return buildList {
                var currentDate = startDate
                repeat(DayOfWeek.entries.size) {
                    add(
                        DashboardState.DayItem(
                            date = currentDate,
                            isSelected = currentDate == selectedDate,
                            isToday = currentDate == today,
                        )
                    )
                    currentDate = currentDate.plusDays(1)
                }
            }
        }
    }
}
