package com.patrykandpatryk.liftapp.feature.exercises.usecase

import com.patrykandpatryk.liftapp.core.search.SearchAlgorithm
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.feature.exercises.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExercisesItem
import java.text.Collator
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class GetExercisesItemsUseCase
@Inject
constructor(
    private val collator: Collator,
    private val repository: ExerciseRepository,
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    private val searchAlgorithm: SearchAlgorithm,
    private val stringProvider: StringProvider,
) {

    operator fun invoke(query: Flow<String>, groupBy: Flow<GroupBy>): Flow<List<ExercisesItem>> =
        combine(flow = repository.getAllExercises(), flow2 = query, flow3 = groupBy) {
                latestExercises,
                latestQuery,
                latestGroupBy ->
                latestExercises.sortByName().search(query = latestQuery).let {
                    (matches, queryMatchPositions) ->
                    if (latestQuery.isNotEmpty()) {
                        return@let matches.mapIndexed { index, match ->
                            toExerciseItem(match, match.id.toString(), queryMatchPositions[index])
                        }
                    }

                    matches
                        .group(groupBy = latestGroupBy)
                        .run { if (latestGroupBy != GroupBy.Name) sortByHeader() else this }
                        .toExerciseItems { queryMatchPositions[it] }
                }
            }
            .flowOn(dispatcher)

    private fun List<Exercise>.search(query: String) =
        searchAlgorithm(
            items = this,
            selector = { exercise -> exercise.displayName },
            query = query,
        )

    private fun List<Exercise>.sortByName() = sortedWith { exercise1, exercise2 ->
        collator.compare(exercise1.displayName, exercise2.displayName)
    }

    private fun List<Exercise>.group(groupBy: GroupBy) =
        when (groupBy) {
            GroupBy.Name -> groupBy { exercise -> exercise.displayName[0].toString() }
            GroupBy.ExerciseType -> groupBy { exercise -> exercise.exerciseType.name }
            GroupBy.MainMuscles -> {
                flatMap { exercise -> exercise.primaryMuscles }
                    .toSet()
                    .associate { muscle ->
                        stringProvider.getMuscleName(muscle) to
                            filter { exercise ->
                                exercise.primaryMuscles.contains(element = muscle)
                            }
                    }
            }
        }

    private fun Map<String, List<Exercise>>.sortByHeader() = toSortedMap { header1, header2 ->
        collator.compare(header1, header2)
    }

    private fun toExerciseItem(exercise: Exercise, key: String, nameHighlightPosition: IntRange) =
        ExercisesItem.Exercise(
            id = exercise.id,
            name = stringProvider.getResolvedName(exercise.name),
            key = key,
            muscles =
                exercise.primaryMuscles.joinToPrettyString(andText = stringProvider.andInAList) {
                    muscle ->
                    stringProvider.getMuscleName(muscle)
                },
            iconRes = exercise.exerciseType.iconRes,
            nameHighlightPosition = nameHighlightPosition,
        )

    private fun Map<String, List<Exercise>>.toExerciseItems(
        getNameHighlightPosition: (Int) -> IntRange
    ): List<ExercisesItem> {
        val idToIndexOfLast = mutableMapOf<Long, Int>()

        return flatMap { (header, exercises) ->
            buildList {
                add(ExercisesItem.Header(header))
                addAll(
                    elements =
                        exercises.mapIndexed { index, exercise ->
                            val occurrenceIndex =
                                (idToIndexOfLast.get(key = exercise.id)?.let { it + 1 } ?: 0).also {
                                    idToIndexOfLast[exercise.id] = it
                                }
                            toExerciseItem(
                                exercise = exercise,
                                key = "${exercise.id}-$occurrenceIndex",
                                nameHighlightPosition = getNameHighlightPosition(index),
                            )
                        }
                )
            }
        }
    }
}
