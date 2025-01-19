package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutRouteData
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.validation.higherThanZero
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.domain.validation.validNumber
import com.patrykandpatryk.liftapp.domain.validation.validNumberHigherThanZero
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.GetWorkoutContract
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetEditableWorkoutUseCase
@Inject
constructor(
    private val contract: GetWorkoutContract,
    private val textFieldStateManager: TextFieldStateManager,
    private val formatter: Formatter,
    private val stringProvider: StringProvider,
    private val workoutRouteData: WorkoutRouteData,
    private val savedStateHandle: SavedStateHandle,
) {
    operator fun invoke(): Flow<EditableWorkout> =
        contract
            .getWorkout(
                workoutRouteData.routineID,
                savedStateHandle[WORKOUT_ID] ?: workoutRouteData.workoutID,
            )
            .map {
                savedStateHandle[WORKOUT_ID] = it.id
                it.editable()
            }

    private suspend fun Workout.editable(): EditableWorkout {
        val exercises =
            exercises.map { exercise ->
                EditableWorkout.Exercise(
                    id = exercise.id,
                    name = exercise.name,
                    exerciseType = exercise.exerciseType,
                    mainMuscles = exercise.mainMuscles,
                    secondaryMuscles = exercise.secondaryMuscles,
                    tertiaryMuscles = exercise.tertiaryMuscles,
                    goal = exercise.goal,
                    sets =
                        exercise.sets.mapIndexed { index, set -> set.editable(exercise.id, index) },
                )
            }

        return EditableWorkout(
            id = id,
            name = name,
            startDate = startDate,
            endDate = endDate,
            notes = notes,
            exercises = exercises,
            pages =
                buildList {
                    exercises.forEach { add(WorkoutPage.Exercise(it, size)) }
                    add(getSummaryPage(exercises, size))
                },
        )
    }

    private suspend fun Workout.getSummaryPage(
        exercises: List<EditableWorkout.Exercise>,
        index: Int,
    ): WorkoutPage.Summary {
        val dateFormat = DateTimeFormatter.ofPattern(stringProvider.dateFormatFull)
        val timeFormat = formatter.getLocalTimeFormatter()

        return WorkoutPage.Summary(
            name = textFieldStateManager.stringTextField(name, validators = { nonEmpty() }),
            startDate =
                textFieldStateManager.localDateField(dateFormat, dateFormat.format(startDate)),
            startTime =
                textFieldStateManager.localTimeField(timeFormat, timeFormat.format(startDate)),
            endDate =
                textFieldStateManager.localDateField(
                    dateFormat,
                    dateFormat.format(endDate ?: LocalDateTime.now()),
                ),
            endTime =
                textFieldStateManager.localTimeField(
                    timeFormat,
                    timeFormat.format(endDate ?: LocalDateTime.now()),
                ),
            notes = textFieldStateManager.stringTextField(notes),
            is24H = formatter.is24H(),
            exercises = exercises,
            index = index,
        )
    }

    private fun ExerciseSet.editable(exerciseId: Long, setIndex: Int): EditableExerciseSet =
        when (this) {
            is ExerciseSet.Weight ->
                EditableExerciseSet.Weight(
                    weight = weight,
                    reps = reps,
                    weightInput =
                        textFieldStateManager.doubleTextField(
                            initialValue = formatDecimal(weight),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "weight"),
                            validators = { validNumberHigherThanZero() },
                        ),
                    repsInput =
                        textFieldStateManager.intTextField(
                            initialValue = formatInteger(reps),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "reps"),
                            validators = { validNumberHigherThanZero() },
                        ),
                    weightUnit = weightUnit,
                )

            is ExerciseSet.Calisthenics ->
                EditableExerciseSet.Calisthenics(
                    weight = weight,
                    bodyWeight = bodyWeight,
                    reps = reps,
                    weightInput =
                        textFieldStateManager.doubleTextField(
                            initialValue = formatDecimal(weight),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "weight"),
                            validators = { validNumber() },
                        ),
                    bodyWeightInput =
                        textFieldStateManager.doubleTextField(
                            initialValue = formatDecimal(bodyWeight),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "body_weight"),
                            validators = { validNumber() },
                        ),
                    repsInput =
                        textFieldStateManager.intTextField(
                            initialValue = formatInteger(reps),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "reps"),
                            validators = { validNumberHigherThanZero() },
                        ),
                    weightUnit = weightUnit,
                )

            is ExerciseSet.Reps ->
                EditableExerciseSet.Reps(
                    reps = reps,
                    repsInput =
                        textFieldStateManager.intTextField(
                            initialValue = formatInteger(reps),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "reps"),
                            validators = { validNumberHigherThanZero() },
                        ),
                )

            is ExerciseSet.Cardio ->
                EditableExerciseSet.Cardio(
                    duration = duration,
                    distance = distance,
                    kcal = kcal,
                    durationInput =
                        textFieldStateManager.longTextField(
                            initialValue = formatInteger(duration.inWholeMilliseconds),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "time"),
                            validators = { higherThanZero() },
                        ),
                    distanceInput =
                        textFieldStateManager.doubleTextField(
                            initialValue = formatDecimal(distance),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "distance"),
                            validators = { validNumberHigherThanZero() },
                        ),
                    kcalInput =
                        textFieldStateManager.doubleTextField(
                            initialValue = formatDecimal(kcal),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "kcal"),
                            validators = { validNumberHigherThanZero() },
                        ),
                    distanceUnit = distanceUnit,
                )

            is ExerciseSet.Time ->
                EditableExerciseSet.Time(
                    duration = duration,
                    timeInput =
                        textFieldStateManager.longTextField(
                            initialValue = formatInteger(duration.inWholeMilliseconds),
                            savedStateKey =
                                getTextFieldStateManagerKey(exerciseId, setIndex, "time"),
                            validators = { higherThanZero() },
                        ),
                )
        }

    private fun formatDecimal(value: Double): String =
        if (value == 0.0) ""
        else formatter.formatNumber(value, format = Formatter.NumberFormat.Decimal)

    private fun formatInteger(value: Number): String =
        if (value == 0) ""
        else formatter.formatNumber(value, format = Formatter.NumberFormat.Integer)

    private fun getTextFieldStateManagerKey(
        exerciseId: Long,
        setIndex: Int,
        inputType: String,
    ): String = "exercise_${exerciseId}_set_${setIndex}_$inputType"

    private companion object {
        const val WORKOUT_ID = "workoutID"
    }
}
