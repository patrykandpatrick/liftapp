package com.patrykandpatrick.liftapp.feature.exercise.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.core.model.MuscleModel
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseStatistics
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSummaryType
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleContainer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer

@Immutable
data class ScreenState(
    val name: String,
    val showDeleteDialog: Boolean,
    override val primaryMuscles: List<Muscle>,
    override val secondaryMuscles: List<Muscle>,
    override val tertiaryMuscles: List<Muscle>,
    val hasExerciseHistory: Boolean,
    val exerciseSetGroups: List<ExerciseSetGroup>,
    val exerciseStatistics: ExerciseStatistics?,
    val cartesianChartModelProducer: CartesianChartModelProducer,
    val dateInterval: DateInterval,
    val dateIntervalOptions: List<DateInterval>,
    val summaryType: ExerciseSummaryType,
    val summaryTypeOptions: List<ExerciseSummaryType>,
) : MuscleContainer {
    val muscles: List<MuscleModel> =
        MuscleModel.create(
            primaryMuscles = primaryMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
        )
}
