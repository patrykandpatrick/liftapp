package com.patrykandpatryk.liftapp.domain.routine

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetRoutineExerciseItemsUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(exerciseIds: List<Long>): Flow<List<RoutineExerciseItem>> =
        exerciseRepository.getRoutineExerciseItems(exerciseIds)
            .flowOn(dispatcher)
}
