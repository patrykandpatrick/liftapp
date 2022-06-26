package com.patrykandpatryk.liftapp.feature.newexercise.di

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.newexercise.mapper.StateToExerciseInsertMapper
import com.patrykandpatryk.liftapp.feature.newexercise.state.NewExerciseState
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NewExerciseModule {

    @Binds
    fun bindStateMapper(mapper: StateToExerciseInsertMapper): Mapper<NewExerciseState.Valid, Exercise.Insert>
}
