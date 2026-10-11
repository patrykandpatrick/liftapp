package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.resource.prettyString
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.progression.LoadAdvice
import com.patrykandpatrick.liftapp.domain.progression.LoadSuggestion
import com.patrykandpatrick.liftapp.domain.progression.StallEscalation
import com.patrykandpatrick.liftapp.domain.progression.SuggestionReason
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * What this set was asked for, above the fields recording what it actually was.
 *
 * Nothing is shown when the plan has nothing to say — a set added mid-session was never prescribed,
 * and an empty card would read as a target of zero.
 *
 * [loadAdvice] completes the prescription with a weight, and never appears without its reason.
 */
@Composable
internal fun SetTargetCard(
    goal: Workout.Goal,
    setIndex: Int,
    exerciseType: ExerciseType,
    modifier: Modifier = Modifier,
    loadAdvice: LoadAdvice? = null,
    massUnit: MassUnit = MassUnit.Kilograms,
    mainMuscle: Muscle? = null,
) {
    val target = goal.targetFor(setIndex) ?: return

    LiftAppCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TargetRow(
                title = stringResource(R.string.workout_set_target_title),
                value = target.prettyString(exerciseType, goal.distanceUnit),
            )

            when (loadAdvice) {
                is LoadSuggestion -> {
                    TargetRow(
                        title = stringResource(R.string.workout_set_target_weight_title),
                        value = LocalFormatter.current.formatWeight(loadAdvice.load, massUnit),
                    )
                    Text(
                        text = loadAdvice.reason.prettyString(massUnit, mainMuscle),
                        style = typography.bodySmall,
                        color = colorScheme.foreground,
                    )
                }
                LoadAdvice.OneMoreSession ->
                    Text(
                        text = stringResource(R.string.load_suggestion_one_more_session),
                        style = typography.labelSmall,
                        color = colorScheme.foregroundVariant,
                    )
                null -> Unit
            }

            if (goal.isStraightSets) {
                Text(
                    text = stringResource(R.string.workout_set_target_straight_sets_note),
                    style = typography.labelSmall,
                    color = colorScheme.foregroundVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun TargetRow(title: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth(),
    ) {
        LiftAppText(
            text = title,
            style = typography.labelMedium,
            color = colorScheme.foregroundVariant,
        )

        LiftAppText(text = value, style = typography.titleMedium)
    }
}

/**
 * Straight sets — every set asking for the same thing — is the one shape where the note is true and
 * worth making: reps and load hold, so the effort has nowhere to go but up. A ramped or backed-off
 * prescription has already accounted for that, and saying it there would be wrong.
 */
private val Workout.Goal.isStraightSets: Boolean
    get() = setTargets.size > 1 && setTargets.distinct().size == 1

@LightAndDarkThemePreview
@Composable
private fun SetTargetCardPreview() {
    PreviewTheme {
        LiftAppBackground {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                SetTargetCard(
                    goal =
                        Workout.Goal.default.copy(
                            setTargets = List(3) { SetTarget(minReps = 8, maxReps = 12, rir = 2) }
                        ),
                    setIndex = 0,
                    exerciseType = ExerciseType.Weight,
                    loadAdvice =
                        LoadSuggestion(
                            load = 82.5,
                            reason = SuggestionReason.LoadUp(previousLoad = 80.0),
                        ),
                )

                SetTargetCard(
                    goal =
                        Workout.Goal.default.copy(
                            setTargets = List(3) { SetTarget(minReps = 8, maxReps = 12, rir = 2) }
                        ),
                    setIndex = 0,
                    exerciseType = ExerciseType.Weight,
                    loadAdvice =
                        LoadSuggestion(
                            load = 80.0,
                            reason =
                                SuggestionReason.Stalled(
                                    step = StallEscalation.ReduceVolumeElsewhere,
                                    sessions = 4,
                                ),
                        ),
                    mainMuscle = Muscle.Chest,
                )

                SetTargetCard(
                    goal =
                        Workout.Goal.default.copy(
                            setTargets = List(3) { SetTarget(minReps = 8, maxReps = 12, rir = 2) }
                        ),
                    setIndex = 0,
                    exerciseType = ExerciseType.Weight,
                    loadAdvice = LoadAdvice.OneMoreSession,
                )

                SetTargetCard(
                    goal =
                        Workout.Goal.default.copy(
                            setTargets =
                                listOf(
                                    SetTarget(minReps = 5, maxReps = 5, rir = 2),
                                    SetTarget(minReps = 8, maxReps = 10, rir = 1),
                                )
                        ),
                    setIndex = 1,
                    exerciseType = ExerciseType.Weight,
                )
            }
        }
    }
}
