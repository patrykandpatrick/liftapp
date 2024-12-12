package com.patrykandpatryk.liftapp.feature.newexercise.di

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.newexercise.mapper.ExerciseToStateMapper
import com.patrykandpatryk.liftapp.feature.newexercise.mapper.StateToExerciseInsertMapper
import com.patrykandpatryk.liftapp.feature.newexercise.mapper.StateToExerciseUpdateMapper
import com.patrykandpatryk.liftapp.feature.newexercise.state.NewExerciseState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface NewExerciseModule {

    @Binds
    fun bindStateToInsertMapper(
        mapper: StateToExerciseInsertMapper
    ): Mapper<NewExerciseState.Valid, Exercise.Insert>

    @Binds
    fun bindToUpdateStateMapper(
        mapper: StateToExerciseUpdateMapper
    ): Mapper<NewExerciseState.Valid, Exercise.Update>

    @Binds fun bindExerciseMapper(mapper: ExerciseToStateMapper): Mapper<Exercise, NewExerciseState>
}
