package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.UpsertExerciseSetContract
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class UpsertExerciseSetUseCase
@Inject
constructor(private val contract: UpsertExerciseSetContract) {
    suspend operator fun invoke(
        workoutID: Long,
        exerciseID: Long,
        set: EditableExerciseSet<ExerciseSet>,
        setIndex: Int,
    ) {
        contract.upsertExerciseSet(workoutID, exerciseID, set.toDomain(), setIndex)
    }

    private fun EditableExerciseSet<ExerciseSet>.toDomain(): ExerciseSet =
        when (this) {
            is EditableExerciseSet.Weight ->
                ExerciseSet.Weight(
                    weight = weightInput.value,
                    reps = repsInput.value,
                    weightUnit = weightUnit,
                    notes = notesInput.value,
                )

            is EditableExerciseSet.Calisthenics ->
                ExerciseSet.Calisthenics(
                    weight = weightInput.value,
                    bodyWeight = bodyWeight,
                    reps = repsInput.value,
                    weightUnit = weightUnit,
                    notes = notesInput.value,
                )

            is EditableExerciseSet.Reps ->
                ExerciseSet.Reps(reps = repsInput.value, notes = notesInput.value)

            is EditableExerciseSet.Cardio ->
                ExerciseSet.Cardio(
                    duration = durationInput.value.milliseconds,
                    distance = distanceInput.value,
                    kcal = kcalInput.value,
                    distanceUnit = distanceUnit,
                    notes = notesInput.value,
                )

            is EditableExerciseSet.Time ->
                ExerciseSet.Time(
                    duration = timeInput.value.milliseconds,
                    notes = notesInput.value,
                )
        }
}
