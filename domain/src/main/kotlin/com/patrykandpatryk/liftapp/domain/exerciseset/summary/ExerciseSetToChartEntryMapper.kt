package com.patrykandpatryk.liftapp.domain.exerciseset.summary

import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType
import javax.inject.Inject

class ExerciseSetToChartEntryMapper
@Inject
constructor(
    private val mappers:
        Map<
            @JvmSuppressWildcards
            ExerciseSummaryType,
            @JvmSuppressWildcards
            ExerciseSetSummaryMapper,
        >
) {

    suspend operator fun invoke(
        type: ExerciseSummaryType,
        input: List<ExerciseSetGroup>,
    ): List<Pair<List<Double>, List<Double>>> {
        return if (input.isEmpty()) {
            emptyList()
        } else {
            mappers[type]?.invoke(input) ?: emptyList()
        }
    }
}
