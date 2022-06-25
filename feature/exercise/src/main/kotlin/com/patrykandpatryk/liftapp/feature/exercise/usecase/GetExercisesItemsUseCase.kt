package com.patrykandpatryk.liftapp.feature.exercise.usecase

import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.exercise.ui.ExercisesItem
import java.text.Collator
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetExercisesItemsUseCase @Inject constructor(
    private val collator: Collator,
    private val repository: ExerciseRepository,
    private val mapExercises: Mapper<Exercise, ExercisesItem.Exercise>,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) {

    operator fun invoke(): Flow<List<ExercisesItem>> = repository
        .getAllExercises()
        .map { exercises ->
            exercises
                .sortedWith { exercise1, exercise2 ->
                    collator.compare(exercise1.name, exercise2.name)
                }
                .groupBy { exercise -> exercise.name[0].toString() }
                .map { (header, exercises) ->
                    buildList {
                        add(ExercisesItem.Header(header))
                        addAll(mapExercises(exercises))
                    }
                }.flatten()
        }.flowOn(dispatcher)
}
