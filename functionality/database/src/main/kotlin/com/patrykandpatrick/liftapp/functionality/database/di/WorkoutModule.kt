package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsContract
import com.patrykandpatrick.liftapp.domain.workout.UpdateWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.UpsertExerciseSetContract
import com.patrykandpatrick.liftapp.domain.workout.UpsertWorkoutGoalContract
import com.patrykandpatrick.liftapp.functionality.database.workout.RoomWorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface WorkoutModule {
    @Binds fun bindGetWorkoutContract(repository: RoomWorkoutRepository): GetWorkoutContract

    @Binds
    fun bindUpsertExerciseSetContract(repository: RoomWorkoutRepository): UpsertExerciseSetContract

    @Binds
    fun bindUpsertWorkoutGoalContract(repository: RoomWorkoutRepository): UpsertWorkoutGoalContract

    @Binds fun bindUpdateWorkoutContract(repository: RoomWorkoutRepository): UpdateWorkoutContract

    @Binds fun bindGetWorkoutsContract(repository: RoomWorkoutRepository): GetWorkoutsContract

    @Binds
    fun bindGetWorkoutsByDateContract(repository: RoomWorkoutRepository): GetWorkoutsByDateContract
}
