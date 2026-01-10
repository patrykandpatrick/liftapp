package com.patrykandpatryk.liftapp.feature.exercise.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatryk.liftapp.core.model.MuscleModel
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.muscle.MuscleContainer

@Immutable
data class ScreenState(
    val name: String,
    val showDeleteDialog: Boolean,
    override val primaryMuscles: List<Muscle>,
    override val secondaryMuscles: List<Muscle>,
    override val tertiaryMuscles: List<Muscle>,
    val exerciseSetGroups: List<ExerciseSetGroup>,
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
