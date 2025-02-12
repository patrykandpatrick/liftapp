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
                        exercise.sets.mapIndexed { index, set ->
                            set.editable(
                                exerciseId = exercise.id,
                                setIndex = index,
                                previousSet = exercise.sets.getOrNull(index - 1),
                            )
                        },
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

    @Suppress("UNCHECKED_CAST")
    private fun ExerciseSet.editable(
        exerciseId: Long,
        setIndex: Int,
        previousSet: ExerciseSet?,
    ): EditableExerciseSet<ExerciseSet> =
        when (this) {
            is ExerciseSet.Weight ->
                editable(exerciseId, setIndex, previousSet as? ExerciseSet.Weight)

            is ExerciseSet.Calisthenics ->
                editable(exerciseId, setIndex, previousSet as? ExerciseSet.Calisthenics)

            is ExerciseSet.Reps -> editable(exerciseId, setIndex, previousSet as? ExerciseSet.Reps)

            is ExerciseSet.Cardio ->
                editable(exerciseId, setIndex, previousSet as? ExerciseSet.Cardio)

            is ExerciseSet.Time -> editable(exerciseId, setIndex, previousSet as? ExerciseSet.Time)
        }
            as EditableExerciseSet<ExerciseSet>

    private fun ExerciseSet.Weight.editable(
        exerciseId: Long,
        setIndex: Int,
        previousSet: ExerciseSet.Weight?,
    ): EditableExerciseSet.Weight =
        EditableExerciseSet.Weight(
            weight = weight,
            reps = reps,
            weightInput =
                textFieldStateManager.doubleTextField(
                    initialValue = formatDecimal(weight),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "weight"),
                    validators = { validNumberHigherThanZero() },
                ),
            repsInput =
                textFieldStateManager.intTextField(
                    initialValue = formatInteger(reps),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "reps"),
                    validators = { validNumberHigherThanZero() },
                ),
            weightUnit = weightUnit,
            suggestions =
                listOfNotNull(
                    previousSet?.let { set ->
                        EditableExerciseSet.SetSuggestion(
                            set,
                            EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                        )
                    }
                ),
        )

    private fun ExerciseSet.Calisthenics.editable(
        exerciseId: Long,
        setIndex: Int,
        previousSet: ExerciseSet.Calisthenics?,
    ): EditableExerciseSet.Calisthenics =
        EditableExerciseSet.Calisthenics(
            weight = weight,
            bodyWeight = bodyWeight,
            reps = reps,
            formattedBodyWeight =
                "${formatDecimal(bodyWeight)}${stringProvider.getDisplayUnit(weightUnit)}",
            weightInput =
                textFieldStateManager.doubleTextField(
                    initialValue = formatDecimal(weight),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "weight"),
                    validators = { validNumber() },
                ),
            repsInput =
                textFieldStateManager.intTextField(
                    initialValue = formatInteger(reps),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "reps"),
                    validators = { validNumberHigherThanZero() },
                ),
            weightUnit = weightUnit,
            suggestions =
                listOfNotNull(
                    previousSet?.let { set ->
                        EditableExerciseSet.SetSuggestion(
                            set,
                            EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                        )
                    }
                ),
        )

    private fun ExerciseSet.Reps.editable(
        exerciseId: Long,
        setIndex: Int,
        previousSet: ExerciseSet.Reps?,
    ): EditableExerciseSet.Reps =
        EditableExerciseSet.Reps(
            reps = reps,
            repsInput =
                textFieldStateManager.intTextField(
                    initialValue = formatInteger(reps),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "reps"),
                    validators = { validNumberHigherThanZero() },
                ),
            suggestions =
                listOfNotNull(
                    previousSet?.let { set ->
                        EditableExerciseSet.SetSuggestion(
                            set,
                            EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                        )
                    }
                ),
        )

    private fun ExerciseSet.Cardio.editable(
        exerciseId: Long,
        setIndex: Int,
        previousSet: ExerciseSet.Cardio?,
    ): EditableExerciseSet.Cardio =
        EditableExerciseSet.Cardio(
            duration = duration,
            distance = distance,
            kcal = kcal,
            durationInput =
                textFieldStateManager.longTextField(
                    initialValue = formatInteger(duration.inWholeMilliseconds),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "time"),
                    validators = { higherThanZero() },
                ),
            distanceInput =
                textFieldStateManager.doubleTextField(
                    initialValue = formatDecimal(distance),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "distance"),
                    validators = { validNumberHigherThanZero() },
                ),
            kcalInput =
                textFieldStateManager.doubleTextField(
                    initialValue = formatDecimal(kcal),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "kcal"),
                    validators = { validNumberHigherThanZero() },
                ),
            distanceUnit = distanceUnit,
            suggestions =
                listOfNotNull(
                    previousSet?.let { set ->
                        EditableExerciseSet.SetSuggestion(
                            set,
                            EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                        )
                    }
                ),
        )

    private fun ExerciseSet.Time.editable(
        exerciseId: Long,
        setIndex: Int,
        previousSet: ExerciseSet.Time?,
    ): EditableExerciseSet.Time =
        EditableExerciseSet.Time(
            duration = duration,
            timeInput =
                textFieldStateManager.longTextField(
                    initialValue = formatInteger(duration.inWholeMilliseconds),
                    savedStateKey = getTextFieldStateManagerKey(exerciseId, setIndex, "time"),
                    validators = { higherThanZero() },
                ),
            suggestions =
                listOfNotNull(
                    previousSet?.let { set ->
                        EditableExerciseSet.SetSuggestion(
                            set,
                            EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                        )
                    }
                ),
        )

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
