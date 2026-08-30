package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.target.GetExerciseTargetsContract
import com.patrykandpatrick.liftapp.domain.target.TargetRepository
import com.patrykandpatrick.liftapp.domain.target.UpsertExerciseTargetsContract
import com.patrykandpatrick.liftapp.functionality.database.target.RoomTargetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface TargetModule {
    @Binds fun bindTargetRepository(repository: RoomTargetRepository): TargetRepository

    @Binds
    fun bindGetExerciseTargetsContract(repository: RoomTargetRepository): GetExerciseTargetsContract

    @Binds
    fun bindUpsertExerciseTargetsContract(
        repository: RoomTargetRepository
    ): UpsertExerciseTargetsContract
}
