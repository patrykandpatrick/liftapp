package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

class GetRoutineExerciseItemsUseCase
@Inject
constructor(
    private val exerciseRepository: ExerciseRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(exerciseIds: List<Long>): Flow<List<RoutineExerciseItem>> =
        if (exerciseIds.isEmpty()) {
                flowOf(emptyList())
            } else {
                exerciseRepository.getRoutineExerciseItems(exerciseIds, true)
            }
            .flowOn(dispatcher)
}
