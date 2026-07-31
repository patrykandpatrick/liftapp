package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup

interface ExerciseSetSummaryMapper {

    suspend operator fun invoke(
        input: List<ExerciseSetGroup>
    ): List<Pair<List<Double>, List<Double>>>
}
