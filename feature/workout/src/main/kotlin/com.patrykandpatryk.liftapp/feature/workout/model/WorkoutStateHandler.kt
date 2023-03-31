package com.patrykandpatryk.liftapp.feature.workout.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import com.patrykandpatryk.liftapp.domain.state.MutableStateHandler
import com.patrykandpatryk.liftapp.domain.di.MainDispatcher
import javax.inject.Inject

class WorkoutStateHandler @Inject constructor(
    @MainDispatcher.Immediate private val mainDispatcher: CoroutineDispatcher,
    exceptionHandler: CoroutineExceptionHandler,
) : MutableStateHandler<WorkoutState, WorkoutIntent, WorkoutEvent>(mainDispatcher, exceptionHandler) {

    override val state: MutableStateFlow<WorkoutState> = MutableStateFlow(WorkoutState())

    override fun handleIntent(intent: WorkoutIntent) {
        when (intent) {
            else -> Unit
        }
    }
}
