package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.workout.GetWorkoutContract
import com.patrykandpatryk.liftapp.domain.workout.UpsertExerciseSetContract
import com.patrykandpatryk.liftapp.domain.workout.UpsertWorkoutGoalContract
import com.patrykandpatryk.liftapp.functionality.database.workout.RoomWorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface WorkoutModule {
    @Binds
    fun bindGetWorkoutContract(repository: RoomWorkoutRepository): GetWorkoutContract

    @Binds
    fun bindUpsertExerciseSetContract(repository: RoomWorkoutRepository): UpsertExerciseSetContract

    @Binds
    fun bindUpsertWorkoutGoalContract(repository: RoomWorkoutRepository): UpsertWorkoutGoalContract
}
