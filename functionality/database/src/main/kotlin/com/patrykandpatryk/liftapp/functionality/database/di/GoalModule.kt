package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.goal.GetExerciseGoalContract
import com.patrykandpatryk.liftapp.domain.goal.SaveGoalContract
import com.patrykandpatryk.liftapp.functionality.database.goal.RoomGoalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface GoalModule {
    @Binds fun bindGetExerciseGoalContract(repository: RoomGoalRepository): GetExerciseGoalContract

    @Binds fun bindSaveGoalContract(repository: RoomGoalRepository): SaveGoalContract
}
