package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.goal.GetExerciseGoalContract
import com.patrykandpatrick.liftapp.domain.goal.SaveGoalContract
import com.patrykandpatrick.liftapp.functionality.database.goal.RoomGoalRepository
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
