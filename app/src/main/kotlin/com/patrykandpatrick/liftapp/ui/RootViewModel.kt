package com.patrykandpatrick.liftapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.logging.LogPublisher
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.core.message.ConfirmationPublisher
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.workout.GetActiveWorkoutsUseCase
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.WorkoutRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class RootViewModel
@Inject
constructor(
    logger: UiLogger,
    confirmationPublisher: ConfirmationPublisher,
    val formatter: Formatter,
    private val getActiveWorkoutsUseCase: GetActiveWorkoutsUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel(), LogPublisher by logger {

    /** Confirmations raised by screens that navigate away as they succeed. */
    val confirmations = confirmationPublisher.messages

    private val _pendingWorkoutStart = MutableStateFlow<PendingWorkoutStart?>(null)
    val pendingWorkoutStart: StateFlow<PendingWorkoutStart?> = _pendingWorkoutStart

    suspend fun interceptWorkoutStart(command: NavigationCommand.Route): Boolean {
        val route = command.route as? WorkoutRouteData ?: return false
        if (route.workoutID != ID_NOT_SET) return false

        val activeWorkout = getActiveWorkoutsUseCase().first().firstOrNull() ?: return false
        _pendingWorkoutStart.value = PendingWorkoutStart(activeWorkout, command)
        return true
    }

    fun cancelWorkoutStart() {
        _pendingWorkoutStart.value = null
    }

    fun continueActiveWorkout() {
        val pending = _pendingWorkoutStart.value ?: return
        _pendingWorkoutStart.value = null
        viewModelScope.launch {
            navigationCommander.navigateTo(
                pending.command.copy(route = Routes.Workout.edit(pending.workout.id))
            )
        }
    }

    data class PendingWorkoutStart(
        val workout: Workout,
        val command: NavigationCommand.Route,
    )
}
