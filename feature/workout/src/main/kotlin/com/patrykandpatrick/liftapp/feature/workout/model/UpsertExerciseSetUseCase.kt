package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.UpsertExerciseSetContract
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class UpsertExerciseSetUseCase
@Inject
constructor(private val contract: UpsertExerciseSetContract) {
    suspend operator fun invoke(
        workoutID: Long,
        exerciseID: Long,
        set: EditableExerciseSet,
        setIndex: Int,
    ) {
        contract.upsertExerciseSet(workoutID, exerciseID, set.toDomain(), setIndex)
    }

    private fun EditableExerciseSet.toDomain(): ExerciseSet =
        when (this) {
            is EditableExerciseSet.Weight ->
                ExerciseSet.Weight(
                    weight = weightInput.value,
                    reps = repsInput.value,
                    weightUnit = weightUnit,
                )

            is EditableExerciseSet.Calisthenics ->
                ExerciseSet.Calisthenics(
                    weight = weightInput.value,
                    bodyWeight = bodyWeightInput.value,
                    reps = repsInput.value,
                    weightUnit = weightUnit,
                )

            is EditableExerciseSet.Reps -> ExerciseSet.Reps(reps = repsInput.value)

            is EditableExerciseSet.Cardio ->
                ExerciseSet.Cardio(
                    duration = durationInput.value.milliseconds,
                    distance = distanceInput.value,
                    kcal = kcalInput.value,
                    distanceUnit = distanceUnit,
                )

            is EditableExerciseSet.Time -> ExerciseSet.Time(duration = timeInput.value.milliseconds)
        }
}
