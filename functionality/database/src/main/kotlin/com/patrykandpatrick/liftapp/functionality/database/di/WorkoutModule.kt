package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.workout.DeleteWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.EditWorkoutItemsContract
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutPageContract
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutsInRangeContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsContract
import com.patrykandpatrick.liftapp.domain.workout.UpdateExerciseNotesContract
import com.patrykandpatrick.liftapp.domain.workout.UpdateWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.UpsertExerciseSetContract
import com.patrykandpatrick.liftapp.domain.workout.UpsertWorkoutGoalContract
import com.patrykandpatrick.liftapp.functionality.database.workout.RoomWorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface WorkoutModule {
    @Binds fun bindGetWorkoutContract(repository: RoomWorkoutRepository): GetWorkoutContract

    @Binds
    fun bindUpsertExerciseSetContract(repository: RoomWorkoutRepository): UpsertExerciseSetContract

    @Binds
    fun bindUpsertWorkoutGoalContract(repository: RoomWorkoutRepository): UpsertWorkoutGoalContract

    @Binds fun bindUpdateWorkoutContract(repository: RoomWorkoutRepository): UpdateWorkoutContract

    @Binds
    fun bindUpdateExerciseNotesContract(
        repository: RoomWorkoutRepository
    ): UpdateExerciseNotesContract

    @Binds fun bindGetWorkoutsContract(repository: RoomWorkoutRepository): GetWorkoutsContract

    @Binds
    fun bindGetPastWorkoutPageContract(
        repository: RoomWorkoutRepository
    ): GetPastWorkoutPageContract

    @Binds
    fun bindGetPastWorkoutsInRangeContract(
        repository: RoomWorkoutRepository
    ): GetPastWorkoutsInRangeContract

    @Binds fun bindDeleteWorkoutContract(repository: RoomWorkoutRepository): DeleteWorkoutContract

    @Binds
    fun bindEditWorkoutItemsContract(repository: RoomWorkoutRepository): EditWorkoutItemsContract

    @Binds
    fun bindGetWorkoutsByDateContract(repository: RoomWorkoutRepository): GetWorkoutsByDateContract
}
