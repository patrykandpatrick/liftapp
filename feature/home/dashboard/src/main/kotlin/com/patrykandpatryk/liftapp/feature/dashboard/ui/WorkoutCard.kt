package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.text.rememberDefaultMarkupProcessor
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutCard(workout: Workout, onClick: (Workout) -> Unit, modifier: Modifier = Modifier) {
    val markupProcessor = LocalMarkupProcessor.current
    LiftAppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick(workout) },
        colors =
            if (workout.isCompleted) LiftAppCardDefaults.cardColors
            else LiftAppCardDefaults.tonalCardColors,
    ) {
        WorkoutStatusWithDate(workout)

        Text(text = workout.name, style = MaterialTheme.typography.titleMedium)

        Text(
            text =
                buildAnnotatedString {
                    workout.exercises.forEachIndexed { index, exercise ->
                        append("• ")
                        append(exercise.name.getDisplayName())
                        append(" • ")
                        append(
                            markupProcessor.toAnnotatedString(
                                stringResource(
                                    R.string.workout_exercise_list_set_format,
                                    exercise.completedSets,
                                    exercise.totalSets,
                                    pluralStringResource(R.plurals.set_count, exercise.totalSets),
                                )
                            )
                        )
                        if (index < workout.exercises.lastIndex) append("\n")
                    }
                },
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 0.dp),
        )

        PlainLiftAppButton(
            onClick = { onClick(workout) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(
                text =
                    if (workout.isCompleted) {
                        stringResource(R.string.action_show)
                    } else {
                        stringResource(R.string.action_continue)
                    }
            )
        }
    }
}

@Composable
fun ColumnScope.WorkoutStatusWithDate(workout: Workout) {
    val markupProcessor = LocalMarkupProcessor.current
    val datePattern = stringResource(R.string.dashboard_workout_date_pattern)
    val dateFormat = remember(datePattern) { DateTimeFormatter.ofPattern(datePattern) }

    Icon(
        painter =
            if (workout.isCompleted) {
                painterResource(R.drawable.ic_check_circle)
            } else {
                painterResource(R.drawable.ic_pending)
            },
        contentDescription = null,
        tint = colorScheme.onSurfaceVariant,
    )

    Text(
        text =
            markupProcessor.toAnnotatedString(
                workout.endDate?.let { date ->
                    stringResource(
                        R.string.dashboard_completed_workout_date_title,
                        date.format(dateFormat),
                    )
                }
                    ?: stringResource(
                        R.string.dashboard_active_workout_date_title,
                        workout.startDate.format(dateFormat),
                    )
            ),
        style = MaterialTheme.typography.labelSmall,
        color = colorScheme.onSurfaceVariant,
    )
}

@LightAndDarkThemePreview
@Composable
private fun CompletedWorkoutCardPreview() {
    LiftAppTheme {
        CompositionLocalProvider(LocalMarkupProcessor provides rememberDefaultMarkupProcessor()) {
            LiftAppBackground {
                WorkoutCard(
                    workout =
                        Workout(
                            id = 0L,
                            routineID = 0L,
                            name = "Push",
                            startDate = LocalDateTime.now(),
                            endDate = LocalDateTime.now(),
                            notes = "",
                            exercises = previewExercises,
                        ),
                    onClick = {},
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun PendingWorkoutCardPreview() {
    LiftAppTheme {
        CompositionLocalProvider(LocalMarkupProcessor provides rememberDefaultMarkupProcessor()) {
            LiftAppBackground {
                WorkoutCard(
                    workout =
                        Workout(
                            id = 0L,
                            routineID = 0L,
                            name = "Push",
                            startDate = LocalDateTime.now(),
                            endDate = null,
                            notes = "",
                            exercises = previewExercises,
                        ),
                    onClick = {},
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

private val previewExercises =
    listOf(
        Workout.Exercise(
            id = 1L,
            name = Name.Raw("Bench Press"),
            exerciseType = ExerciseType.Weight,
            mainMuscles = emptyList(),
            secondaryMuscles = emptyList(),
            tertiaryMuscles = emptyList(),
            goal = Workout.Goal.default,
            sets =
                listOf(
                    ExerciseSet.Weight(100.0, 10, MassUnit.Kilograms),
                    ExerciseSet.Weight(100.0, 10, MassUnit.Kilograms),
                    ExerciseSet.Weight(100.0, 10, MassUnit.Kilograms),
                ),
        ),
        Workout.Exercise(
            id = 1L,
            name = Name.Raw("Overhead Press"),
            exerciseType = ExerciseType.Weight,
            mainMuscles = emptyList(),
            secondaryMuscles = emptyList(),
            tertiaryMuscles = emptyList(),
            goal = Workout.Goal.default,
            sets =
                listOf(
                    ExerciseSet.Weight(70.0, 10, MassUnit.Kilograms),
                    ExerciseSet.Weight(70.0, 10, MassUnit.Kilograms),
                    ExerciseSet.Weight(70.0, 10, MassUnit.Kilograms),
                ),
        ),
        Workout.Exercise(
            id = 1L,
            name = Name.Raw("Incline Dumbbell Bench Press"),
            exerciseType = ExerciseType.Weight,
            mainMuscles = emptyList(),
            secondaryMuscles = emptyList(),
            tertiaryMuscles = emptyList(),
            goal = Workout.Goal.default,
            sets =
                listOf(
                    ExerciseSet.Weight(75.0, 10, MassUnit.Kilograms),
                    ExerciseSet.Weight(75.0, 10, MassUnit.Kilograms),
                    ExerciseSet.Weight(75.0, 10, MassUnit.Kilograms),
                ),
        ),
    )
