package com.patrykandpatryk.liftapp.feature.exercises.usecase

import android.app.Application
import com.patrykandpatryk.liftapp.core.search.SearchAlgorithm
import com.patrykandpatryk.liftapp.core.ui.resource.getName
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExercisesItem
import com.patrykandpatryk.vico.core.extension.orZero
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import java.text.Collator
import javax.inject.Inject

class GetExercisesItemsUseCase @Inject constructor(
    private val collator: Collator,
    private val repository: ExerciseRepository,
    private val mapExercises: Mapper<Pair<Exercise, String>, ExercisesItem.Exercise>,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val searchAlgorithm: SearchAlgorithm,
    private val application: Application,
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
                if (latestQuery.isNotEmpty()) return@run mapExercises(
                    input = map { exercise ->
                        exercise to exercise.id.toString()
                    },
                )
                group(groupBy = latestGroupBy)
                    .run { if (latestGroupBy != GroupBy.Name) sortByHeader() else this }
                    .toExerciseItems()
            }
    }.flowOn(dispatcher)

    private fun List<Exercise>.search(query: String) = searchAlgorithm(
        entities = this,
        selector = { exercise -> exercise.displayName },
        query = query,
    )

    private fun List<Exercise>.sortByName() = sortedWith { exercise1, exercise2 ->
        collator.compare(
            exercise1.displayName,
            exercise2.displayName,
        )
    }

    private fun List<Exercise>.group(groupBy: GroupBy) = when (groupBy) {
        GroupBy.Name -> groupBy { exercise -> exercise.displayName[0].toString() }
        GroupBy.ExerciseType -> groupBy { exercise -> exercise.exerciseType.name }
        GroupBy.MainMuscles -> {

            flatMap { exercise -> exercise.mainMuscles }
                .toSet()
                .associate { muscle ->
                    muscle.getName(application) to filter { exercise ->
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

    private fun Map<String, List<Exercise>>.toExerciseItems(): List<ExercisesItem> {

        val idToIndexOfLast = mutableMapOf<Long, Int>()

        return flatMap { (header, exercises) ->
            buildList {
                add(ExercisesItem.Header(header))
                addAll(
                    elements = exercises.map { exercise ->
                        val occurrenceIndex = idToIndexOfLast
                            .get(key = exercise.id)
                            ?.let { index -> index + 1 }
                            .orZero
                            .also { index -> idToIndexOfLast[exercise.id] = index }
                        mapExercises(exercise to "${exercise.id}-$occurrenceIndex")
                    },
                )
            }
        }
    }
}
