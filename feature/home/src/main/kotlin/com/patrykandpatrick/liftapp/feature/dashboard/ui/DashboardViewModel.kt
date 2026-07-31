package com.patrykandpatrick.liftapp.feature.dashboard.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.domain.date.DAYS_IN_WEEK
import com.patrykandpatrick.liftapp.domain.date.GetFirstDayOfWeekUseCase
import com.patrykandpatrick.liftapp.domain.date.invoke
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.workout.DeleteWorkoutUseCase
import com.patrykandpatrick.liftapp.domain.workout.GetActiveWorkoutsUseCase
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutsUseCase
import com.patrykandpatrick.liftapp.feature.dashboard.model.Action
import com.patrykandpatrick.liftapp.feature.dashboard.model.DashboardState
import com.patrykandpatrick.liftapp.feature.dashboard.model.DashboardWeek
import com.patrykandpatrick.liftapp.feature.dashboard.model.GetDashboardStatisticsUseCase
import com.patrykandpatrick.liftapp.feature.dashboard.model.GetPlanScheduleItemUseCase
import com.patrykandpatrick.liftapp.feature.dashboard.model.PlanScheduleItem
import com.patrykandpatrick.liftapp.feature.dashboard.model.WorkoutTarget
import com.patrykandpatrick.liftapp.feature.home.currentDateFlow
import com.patrykandpatrick.liftapp.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
    getDashboardStatisticsUseCase: GetDashboardStatisticsUseCase,
    getFirstDayOfWeekUseCase: GetFirstDayOfWeekUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    getActivePlanUseCase: GetActivePlanUseCase,
) : ViewModel(coroutineScope) {

    private val selectedDate =
        savedStateHandle.getMutableStateFlow("selected_date", LocalDate.now())

    private val activeWorkoutsAndTarget =
        currentDateFlow().flatMapLatest { currentDate ->
            combine(
                getActiveWorkoutsUseCase(),
                getActivePlanUseCase(),
                getPlanScheduleItemUseCase(currentDate),
            ) { activeWorkouts, activePlan, planScheduleItem ->
                val completedPlannedWorkout =
                    (planScheduleItem as? PlanScheduleItem.Routine)?.workout != null
                val target =
                    activeWorkouts.firstOrNull()?.let { WorkoutTarget.ActiveWorkout(it.id) }
                        ?: getNextRoutineID(
                                activePlan = activePlan,
                                today = currentDate,
                                skipCurrentPlanItem = completedPlannedWorkout,
                            )
                            ?.let(WorkoutTarget::PlannedRoutine)
                activeWorkouts to target
            }
        }

    val state =
        combine(
                activeWorkoutsAndTarget,
                getPastWorkoutsUseCase(limit = RECENT_WORKOUT_COUNT + 1),
                combine(selectedDate, getFirstDayOfWeekUseCase(), ::Pair),
                selectedDate.flatMapLatest { date -> getPlanScheduleItemUseCase(date) },
                currentDateFlow().flatMapLatest { getDashboardStatisticsUseCase(it) },
            ) {
                (activeWorkouts, workoutTarget),
                pastWorkouts,
                (selectedDate, firstDayOfWeek),
                planItem,
                statistics ->
                val planItemWorkout = (planItem as? PlanScheduleItem.Routine)?.workout
                // One more than the dashboard shows is read, so that the journal can be offered
                // when it holds a workout past the recent-workout cap.
                DashboardState(
                    statistics = statistics,
                    dayItems = getWeekDays(selectedDate, firstDayOfWeek),
                    selectedDate = selectedDate,
                    activeWorkouts = activeWorkouts.filter { it.id != planItemWorkout?.id },
                    pastWorkouts = pastWorkouts.take(RECENT_WORKOUT_COUNT),
                    hasMorePastWorkouts = pastWorkouts.size > RECENT_WORKOUT_COUNT,
                    planScheduleItem = planItem,
                    workoutTarget = workoutTarget,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            is Action.NewWorkout -> newWorkout(action.routineID)
            is Action.GoToWorkout -> goToWorkout(action.workoutID)
            is Action.DeleteWorkout -> deleteWorkout(action.workoutID)
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

    private fun deleteWorkout(workoutID: Long) {
        viewModelScope.launch { deleteWorkoutUseCase(workoutID) }
    }

    private fun goToRoutine(routineID: Long) {
        navigate(Routes.Routine.details(routineID))
    }

    private fun navigate(route: Any) {
        viewModelScope.launch { navigationCommander.navigateTo(route) }
    }

    companion object {
        /** How many finished workouts the dashboard shows, as the published app also showed. */
        const val RECENT_WORKOUT_COUNT = 3

        internal fun getWeekDays(
            selectedDate: LocalDate,
            firstDayOfWeek: DayOfWeek,
        ): List<DashboardState.DayItem> {
            val today = LocalDate.now()
            val startDate = DashboardWeek.startOf(today, firstDayOfWeek)

            return buildList {
                var currentDate = startDate
                repeat(DAYS_IN_WEEK) {
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

        /**
         * Returns the next routine remaining in the active schedule, skipping rest days just as the
         * published app's primary workout action did.
         */
        internal fun getNextRoutineID(
            activePlan: Pair<ActivePlan, Plan>?,
            today: LocalDate = LocalDate.now(),
            skipCurrentPlanItem: Boolean = false,
        ): Long? {
            val (selection, plan) = activePlan ?: return null
            if (plan.items.isEmpty()) return null

            val elapsedDays = ChronoUnit.DAYS.between(selection.startDate, today).toInt()
            val scheduledDayCount = plan.items.size * selection.cycleCount
            if (elapsedDays !in 0 until scheduledDayCount) return null

            val firstDayIndex = elapsedDays + if (skipCurrentPlanItem) 1 else 0
            return (firstDayIndex until scheduledDayCount).firstNotNullOfOrNull { dayIndex ->
                (plan.items[dayIndex % plan.items.size] as? Plan.Item.Routine)?.routine?.id
            }
        }
    }
}
