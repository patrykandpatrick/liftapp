package com.patrykandpatryk.liftapp.feature.exercise.usecase

import com.patrykandpatryk.liftapp.core.search.SearchAlgorithm
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.exercise.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercise.ui.ExercisesItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import java.text.Collator
import javax.inject.Inject

class GetExercisesItemsUseCase @Inject constructor(
    private val collator: Collator,
    private val repository: ExerciseRepository,
    private val mapExercises: Mapper<Exercise, ExercisesItem.Exercise>,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val searchAlgorithm: SearchAlgorithm,
) {

    operator fun invoke(
        query: Flow<String>,
        groupBy: Flow<GroupBy>,
    ): Flow<List<ExercisesItem>> = combine(
        flow = repository.getAllExercises(),
        flow2 = query,
        flow3 = groupBy,
    ) { latestExercises, latestQuery, latestGroupBy ->

        latestExercises
            .sortByName()
            .search(query = latestQuery)
            .run {
                if (latestQuery.isNotEmpty()) return@run mapExercises(input = this)
                group(groupBy = latestGroupBy)
                    .run { if (latestGroupBy != GroupBy.Name) sortByHeader() else this }
                    .toExerciseItems()
            }
    }.flowOn(dispatcher)

    private fun List<Exercise>.search(query: String) = searchAlgorithm(
        entities = this,
        selector = { exercise -> exercise.name },
        query = query,
    )

    private fun List<Exercise>.sortByName() = sortedWith { exercise1, exercise2 ->
        collator.compare(
            exercise1.name,
            exercise2.name,
        )
    }

    private fun List<Exercise>.group(groupBy: GroupBy) = when (groupBy) {
        GroupBy.Name -> groupBy { exercise -> exercise.name[0].toString() }
        GroupBy.ExerciseType -> groupBy { exercise -> exercise.exerciseType.name }
        GroupBy.MainMuscles -> {

            flatMap { exercise -> exercise.mainMuscles }
                .toSet()
                .associate { muscle ->
                    muscle.name to filter { exercise ->
                        exercise.mainMuscles.contains(element = muscle)
                    }
                }
        }
    }

    private fun Map<String, List<Exercise>>.sortByHeader() = toSortedMap { header1, header2 ->
        collator.compare(
            header1,
            header2,
        )
    }

    private fun Map<String, List<Exercise>>.toExerciseItems() = flatMap { (header, exercises) ->
        buildList {
            add(ExercisesItem.Header(header))
            addAll(mapExercises(exercises))
        }
    }
}
