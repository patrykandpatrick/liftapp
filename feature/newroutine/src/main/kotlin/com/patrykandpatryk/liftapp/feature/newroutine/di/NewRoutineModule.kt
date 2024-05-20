package com.patrykandpatryk.liftapp.feature.newroutine.di

import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface NewRoutineModule {

    @Binds
    fun bindNonEmptyCollectionValidator(
        validator: NonEmptyCollectionValidator<RoutineExerciseItem, List<RoutineExerciseItem>>,
    ): Validator<List<RoutineExerciseItem>>
}
