package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExerciseIDsContract
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExercisesContract
import com.patrykandpatryk.liftapp.domain.routine.UpsertRoutineWithExerciseIdsContract
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
    fun bindGetRoutineNameContract(
        repository: RoomRoutineRepository
    ): GetRoutineWithExerciseIDsContract

    @Binds
    fun bindUpsertRoutineWithExerciseIdsContract(
        repository: RoomRoutineRepository
    ): UpsertRoutineWithExerciseIdsContract
}
