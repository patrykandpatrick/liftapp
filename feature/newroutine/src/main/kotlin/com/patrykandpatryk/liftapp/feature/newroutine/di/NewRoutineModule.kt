package com.patrykandpatryk.liftapp.feature.newroutine.di

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface NewRoutineModule {

    @Binds
    fun bindNonEmptyCollectionValidator(
        validator: NonEmptyCollectionValidator<RoutineExerciseItem, List<RoutineExerciseItem>>,
    ): Validator<List<RoutineExerciseItem>>

    companion object {

        @Provides
        @RoutineId
        fun provideRoutineId(savedStateHandle: SavedStateHandle): Long =
            savedStateHandle.get<Long>(Routes.ARG_ID) ?: ID_NOT_SET
    }
}
