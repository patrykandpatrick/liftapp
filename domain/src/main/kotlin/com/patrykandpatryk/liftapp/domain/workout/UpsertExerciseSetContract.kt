package com.patrykandpatryk.liftapp.domain.workout

fun interface UpsertExerciseSetContract {
    suspend fun upsertExerciseSet(
        workoutID: Long,
        exerciseId: Long,
        set: ExerciseSet,
        setIndex: Int,
    )
}
