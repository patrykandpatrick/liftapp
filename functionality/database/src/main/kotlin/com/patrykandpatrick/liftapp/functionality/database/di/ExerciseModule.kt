package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.exercise.GetExerciseNameAndTypeContract
import com.patrykandpatrick.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatrick.liftapp.domain.exercise.GetRoutineExercisesUseCase
import com.patrykandpatrick.liftapp.domain.exerciseset.GetExerciseSetsUseCase
import com.patrykandpatrick.liftapp.functionality.database.exercise.RoomExerciseRepository
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

    @Binds fun bindGetExerciseUseCase(repository: RoomExerciseRepository): GetExerciseUseCase

    @Binds
    fun bindGetRoutineExercisesUseCase(
        repository: RoomExerciseRepository
    ): GetRoutineExercisesUseCase

    @Binds
    fun bindGetExerciseSetsUseCase(repository: RoomExerciseRepository): GetExerciseSetsUseCase
}
