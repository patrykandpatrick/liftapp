package com.patrykandpatryk.liftapp.domain.exerciseset.summary

import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
interface SummaryModule {

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.OneRepMax)
    fun bindOneRepMaxMapper(mapper: OneRepMaxExerciseSetSummaryMapper): ExerciseSetSummaryMapper

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.TotalVolume)
    fun bindTotalVolumeMapper(mapper: TotalVolumeExerciseSetSummaryMapper): ExerciseSetSummaryMapper

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.TotalReps)
    fun bindRepsMapper(mapper: TotalRepsExerciseSetSummaryMapper): ExerciseSetSummaryMapper

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.AvgPace)
    fun bindAveragePaceMapper(mapper: AveragePaceExerciseSetSummaryMapper): ExerciseSetSummaryMapper

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.AvgSpeed)
    fun bindAverageSpeedMapper(
        mapper: AverageSpeedExerciseSetSummaryMapper
    ): ExerciseSetSummaryMapper

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.TotalDistance)
    fun bindTotalDistanceMapper(
        mapper: TotalDistanceExerciseSetSummaryMapper
    ): ExerciseSetSummaryMapper

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.TotalDuration)
    fun bindTotalDurationMapper(
        mapper: TotalDurationExerciseSetSummaryMapper
    ): ExerciseSetSummaryMapper

    @Binds
    @IntoMap
    @SummaryKey(ExerciseSummaryType.TotalCalories)
    fun bindTotalCaloriesMapper(
        mapper: TotalCaloriesExerciseSetSummaryMapper
    ): ExerciseSetSummaryMapper
}

@MapKey annotation class SummaryKey(val key: ExerciseSummaryType)
