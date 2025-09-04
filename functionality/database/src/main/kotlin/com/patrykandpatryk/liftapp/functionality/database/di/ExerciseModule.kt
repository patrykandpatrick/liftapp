package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseNameAndTypeContract
import com.patrykandpatryk.liftapp.domain.exercise.GetRoutineExercisesUseCase
import com.patrykandpatryk.liftapp.domain.exerciseset.GetExerciseSetsUseCase
import com.patrykandpatryk.liftapp.functionality.database.exercise.RoomExerciseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ExerciseModule {
    @Binds
    fun bindGetExerciseNameContract(
        repository: RoomExerciseRepository
    ): GetExerciseNameAndTypeContract

    @Binds
    fun bindGetRoutineExercisesUseCase(
        repository: RoomExerciseRepository
    ): GetRoutineExercisesUseCase

    @Binds
    fun bindGetExerciseSetsUseCase(repository: RoomExerciseRepository): GetExerciseSetsUseCase
}
