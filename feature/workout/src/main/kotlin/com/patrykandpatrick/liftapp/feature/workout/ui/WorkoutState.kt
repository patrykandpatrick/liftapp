package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.domain.workout.GetWorkoutUseCase
import com.patrykandpatryk.liftapp.domain.workout.UpsertGoalSetsUseCase
import com.patrykandpatryk.liftapp.domain.workout.Workout
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutState(
    workout: Flow<Workout>,
    private val updateGoalSets: suspend (workoutID: Long, exercise: Workout.Exercise, setCount: Int) -> Unit,
    private val coroutineScope: CoroutineScope,
) {

    val workout: StateFlow<Workout?> = workout
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)

    fun increaseSetCount(exercise: Workout.Exercise) {
        coroutineScope.launch {
            updateGoalSets(getWorkout().id, exercise, exercise.goal.sets + 1)
        }
    }

    fun decreaseSetCount(exercise: Workout.Exercise) {
        coroutineScope.launch {
            updateGoalSets(getWorkout().id, exercise, (exercise.goal.sets - 1).coerceAtLeast(1))
        }
    }

    private suspend fun getWorkout(): Workout = workout.filterNotNull().first()

    @AssistedInject
    constructor(
        @Assisted("routineID") routineID: Long,
        @Assisted("workoutID") workoutID: Long,
        @Assisted coroutineScope: CoroutineScope,
        getWorkoutUseCase: GetWorkoutUseCase,
        upsertGoalSetsUseCase: UpsertGoalSetsUseCase,
        savedStateHandle: SavedStateHandle,
    ) : this(
        workout = getWorkout(routineID, workoutID, getWorkoutUseCase, savedStateHandle),
        updateGoalSets = { workoutID, exercise, sets -> upsertGoalSetsUseCase(workoutID, exercise, sets) },
        coroutineScope = coroutineScope,
    )

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("routineID") routineID: Long,
            @Assisted("workoutID") workoutID: Long,
            coroutineScope: CoroutineScope,
        ): WorkoutState
    }

    companion object {
        private const val WORKOUT_ID_KEY = "workoutID"

        private fun getWorkout(
            routineID: Long,
            workoutID: Long,
            getWorkoutUseCase: GetWorkoutUseCase,
            savedStateHandle: SavedStateHandle
        ): Flow<Workout> = flow {
            val savedWorkoutID = (savedStateHandle.get<Long>(WORKOUT_ID_KEY) ?: workoutID).takeIf { it != 0L }
            emitAll(
                getWorkoutUseCase(routineID, savedWorkoutID)
                    .onEach { workout -> savedStateHandle[WORKOUT_ID_KEY] = workout.id }
            )
        }
    }
}
