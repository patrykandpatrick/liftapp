package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExerciseIDsUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutinesWithExerciseNamesContract
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineWithExerciseIdsUseCase
import com.patrykandpatrick.liftapp.functionality.database.routine.RoomRoutineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RoutineModule {
    @Binds
    fun bindGetRoutineWithExercisesUseCase(
        repository: RoomRoutineRepository
    ): GetRoutineWithExercisesUseCase

    @Binds
    fun bindGetRoutineWithExerciseIDsUseCase(
        repository: RoomRoutineRepository
    ): GetRoutineWithExerciseIDsUseCase

    @Binds
    fun bindUpsertRoutineWithExerciseIdsUseCase(
        repository: RoomRoutineRepository
    ): UpsertRoutineWithExerciseIdsUseCase

    @Binds
    fun bindGetRoutinesWithExerciseNamesContract(
        repository: RoomRoutineRepository
    ): GetRoutinesWithExerciseNamesContract

    @Binds fun bindDeleteRoutineContract(repository: RoomRoutineRepository): DeleteRoutineUseCase
}
