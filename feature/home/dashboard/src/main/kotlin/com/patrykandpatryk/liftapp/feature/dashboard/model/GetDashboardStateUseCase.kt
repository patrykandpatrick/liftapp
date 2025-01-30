package com.patrykandpatryk.liftapp.feature.dashboard.model

import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.workout.GetActiveWorkoutsUseCase
import com.patrykandpatryk.liftapp.domain.workout.GetPastWorkoutsUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class GetDashboardStateUseCase
@Inject
constructor(
    private val getActiveWorkoutsUseCase: GetActiveWorkoutsUseCase,
    private val getPastWorkoutsUseCase: GetPastWorkoutsUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val coroutineExceptionHandler: CoroutineExceptionHandler,
) {
    operator fun invoke(): Flow<DashboardState> =
        combine(getActiveWorkoutsUseCase(), getPastWorkoutsUseCase()) { activeWorkouts, pastWorkouts
                ->
                DashboardState(activeWorkouts = activeWorkouts, pastWorkouts = pastWorkouts)
            }
            .flowOn(dispatcher + coroutineExceptionHandler)
}
