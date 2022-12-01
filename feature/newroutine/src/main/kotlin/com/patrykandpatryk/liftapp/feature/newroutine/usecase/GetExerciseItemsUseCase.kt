package com.patrykandpatryk.liftapp.feature.newroutine.usecase

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.newroutine.ui.ExerciseItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetExerciseItemsUseCase @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseToItemMapper: Mapper<Exercise, ExerciseItem>,
) {

    operator fun invoke(exerciseIds: List<Long>): Flow<List<ExerciseItem>> =
        exerciseRepository.getExercises(exerciseIds)
            .map(exerciseToItemMapper::invoke)
}
