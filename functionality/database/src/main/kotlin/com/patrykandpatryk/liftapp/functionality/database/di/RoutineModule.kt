package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExerciseIDsUseCase
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExercisesContract
import com.patrykandpatryk.liftapp.domain.routine.GetRoutinesWithExerciseNamesContract
import com.patrykandpatryk.liftapp.domain.routine.UpsertRoutineWithExerciseIdsUseCase
import com.patrykandpatryk.liftapp.functionality.database.routine.RoomRoutineRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RoutineModule {
    @Binds
    fun bindGetRoutineWithExercisesContract(
        repository: RoomRoutineRepository
    ): GetRoutineWithExercisesContract

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
