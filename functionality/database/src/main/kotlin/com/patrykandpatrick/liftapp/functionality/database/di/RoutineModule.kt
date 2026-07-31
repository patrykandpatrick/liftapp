package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.GetRoutinesWithExerciseNamesContract
import com.patrykandpatrick.liftapp.domain.routine.ReorderRoutinesUseCase
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineUseCase
import com.patrykandpatrick.liftapp.domain.routine.UpsertRoutineWithItemsUseCase
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
    fun bindGetRoutineWithItemsUseCase(
        repository: RoomRoutineRepository
    ): GetRoutineWithItemsUseCase

    @Binds fun bindUpsertRoutineUseCase(repository: RoomRoutineRepository): UpsertRoutineUseCase

    @Binds
    fun bindUpsertRoutineWithItemsUseCase(
        repository: RoomRoutineRepository
    ): UpsertRoutineWithItemsUseCase

    @Binds
    fun bindGetRoutinesWithExerciseNamesContract(
        repository: RoomRoutineRepository
    ): GetRoutinesWithExerciseNamesContract

    @Binds fun bindReorderRoutinesUseCase(repository: RoomRoutineRepository): ReorderRoutinesUseCase

    @Binds fun bindDeleteRoutineContract(repository: RoomRoutineRepository): DeleteRoutineUseCase
}
