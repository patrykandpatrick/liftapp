package com.patrykandpatrick.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.getDisplayName
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.core.text.rememberDefaultMarkupProcessor
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.SinHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.TextComponent
import com.patrykandpatrick.liftapp.ui.component.appendBulletSeparator
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.CheckCircle
import com.patrykandpatrick.liftapp.ui.icons.CircleFading
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutCard(
    workout: Workout,
    onClick: (Workout) -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: ((Workout) -> Unit)? = null,
) {
    val markupProcessor = LocalMarkupProcessor.current
    LiftAppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick(workout) },
        onLongClick = onLongClick?.let { { it(workout) } },
        colors =
            if (workout.isCompleted) {
                LiftAppCardDefaults.cardColors
            } else {
                LiftAppCardDefaults.tonalCardColors
            },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            WorkoutStatusWithDate(workout)
            LiftAppText(text = workout.name, style = MaterialTheme.typography.titleMedium)
        }

        LiftAppText(
            text =
                buildAnnotatedString {
                    val exerciseNamesWithSets =
                        workout.exercises.map { exercise ->
                            exercise.name.getDisplayName() to
                                markupProcessor.toAnnotatedString(
                                    stringResource(
                                        R.string.workout_exercise_list_set_format,
                                        exercise.completedSets,
                                        exercise.totalSets,
                                        pluralStringResource(
                                            R.plurals.set_count,
                                            exercise.totalSets,
                                        ),
                                    )
                                )
                        }
                    withBulletList(bullet = TextComponent.listBullet) {
                        exerciseNamesWithSets.forEach { (name, sets) ->
                            withBulletListItem {
                                append(name)
                                appendBulletSeparator()
                                append(sets)
                            }
                        }
                    }
                },
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
            color = colorScheme.foregroundVariant,
            modifier = Modifier.padding(top = 0.dp),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.align(Alignment.End).width(IntrinsicSize.Min),
        ) {
            LiftAppText(
                text =
                    if (workout.isCompleted) {
                        stringResource(R.string.action_show)
                    } else {
                        stringResource(R.string.action_continue)
                    },
                style = MaterialTheme.typography.labelLarge,
            )
            SinHorizontalDivider(
                color = colorScheme.primary,
                sinHeight = 4.dp,
                thickness = dimens.button.underlineWidth,
                sinPeriodLength = 1.5.dp,
            )
        }
    }
}

@Composable
fun WorkoutStatusWithDate(workout: Workout) {
    val markupProcessor = LocalMarkupProcessor.current
    val datePattern = stringResource(R.string.dashboard_workout_date_pattern)
    val dateFormat = remember(datePattern) { DateTimeFormatter.ofPattern(datePattern) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector =
                if (workout.isCompleted) {
                    LiftAppIcons.CheckCircle
                } else {
                    LiftAppIcons.CircleFading
                },
            contentDescription = null,
            tint = colorScheme.foregroundVariant,
        )

        LiftAppText(
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
            color = colorScheme.foregroundVariant,
        )
    }
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
