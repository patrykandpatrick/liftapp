package com.patrykandpatryk.liftapp.domain.exerciseset.summary

import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSetGroup

interface ExerciseSetSummaryMapper {

    suspend operator fun invoke(
        input: List<ExerciseSetGroup>
    ): List<Pair<List<Double>, List<Double>>>
}
