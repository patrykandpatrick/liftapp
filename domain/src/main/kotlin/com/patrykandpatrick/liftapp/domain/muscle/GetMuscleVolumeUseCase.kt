package com.patrykandpatrick.liftapp.domain.muscle

import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutsInRangeContract
import com.patrykandpatrick.liftapp.domain.workout.Workout
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Working sets per muscle across the workouts *finished* in the window. A workout still in progress
 * contributes nothing at all — the query drops any row without an end date.
 *
 * The result is always stated per week, the only rate the volume landmarks are given in.
 */
class GetMuscleVolumeUseCase
@Inject
constructor(private val getPastWorkouts: GetPastWorkoutsInRangeContract) {

    operator fun invoke(dateInterval: DateInterval): Flow<MuscleVolume> {
        // Read together, once: each is a getter over the current date, so re-reading the divisor
        // when a result arrives would divide one period's work by another period's length.
        val start = dateInterval.periodStartTime
        val end = dateInterval.periodEndTime
        val elapsedDays = dateInterval.elapsedDays

        return getPastWorkouts.getPastWorkouts(start, end).map { workouts ->
            workouts
                .fold(MuscleVolume.Empty) { volume, workout -> volume + workout.volume() }
                .perWeek(elapsedDays)
        }
    }

    private fun Workout.volume(): MuscleVolume =
        exercises.fold(MuscleVolume.Empty) { volume, exercise -> volume + exercise.volume() }

    /** A five-kilometre run is not one set of quadriceps, so cardio credits nothing. */
    private fun Workout.Exercise.volume(): MuscleVolume =
        if (exerciseType == ExerciseType.Cardio) {
            MuscleVolume.Empty
        } else {
            MuscleVolume.of(
                sets = sets.count { set -> set.isCompleted },
                primaryMuscles = mainMuscles,
                secondaryMuscles = secondaryMuscles,
                tertiaryMuscles = tertiaryMuscles,
            )
        }
}
