package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.progression.StallEscalation
import com.patrykandpatrick.liftapp.domain.progression.SuggestionReason
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * The sentence shown beside a suggested load. [mainMuscle] is the muscle a stall may be
 * over-committed elsewhere; without one the sentence says "this muscle".
 */
@Composable
fun SuggestionReason.prettyString(massUnit: MassUnit, mainMuscle: Muscle?): String =
    when (this) {
        is SuggestionReason.LoadUp ->
            stringResource(
                if (easierThanAsked) {
                    R.string.load_suggestion_load_up_easier
                } else {
                    R.string.load_suggestion_load_up
                },
                weight(previousLoad, massUnit),
            )
        is SuggestionReason.Hold ->
            if (harderThanAsked) {
                stringResource(
                    R.string.load_suggestion_hold_harder,
                    achievedReps,
                    reps(achievedReps),
                )
            } else {
                stringResource(R.string.load_suggestion_hold, achievedReps, maxReps, reps(maxReps))
            }
        is SuggestionReason.BackOff ->
            if (harderThanAsked) {
                stringResource(
                    R.string.load_suggestion_back_off_harder,
                    achievedReps,
                    reps(achievedReps),
                )
            } else {
                stringResource(
                    R.string.load_suggestion_back_off,
                    achievedReps,
                    reps(achievedReps),
                    minReps,
                )
            }
        is SuggestionReason.Capped ->
            stringResource(
                R.string.load_suggestion_capped,
                within.prettyString(massUnit, mainMuscle),
                weight(uncapped, massUnit),
            )
        is SuggestionReason.Stalled -> stalled(step, sessions, mainMuscle)
        is SuggestionReason.Deload ->
            stringResource(R.string.load_suggestion_deload, weight(previousLoad, massUnit))
        is SuggestionReason.PercentOfOneRepMax ->
            stringResource(
                R.string.load_suggestion_percent,
                percent.roundToInt(),
                // A one-rep max estimate claims no more precision than a tenth of a unit.
                weight(round(oneRepMax * 10) / 10, massUnit),
            )
    }

@Composable
private fun stalled(step: StallEscalation, sessions: Int, mainMuscle: Muscle?): String {
    val count = pluralStringResource(R.plurals.session_count, sessions)
    return when (step) {
        StallEscalation.CheckEffort ->
            stringResource(R.string.load_suggestion_stalled_check_effort, sessions, count)
        StallEscalation.DeloadExercise ->
            stringResource(R.string.load_suggestion_stalled_deload, sessions, count)
        StallEscalation.ReduceVolumeElsewhere ->
            if (mainMuscle != null) {
                stringResource(
                    R.string.load_suggestion_stalled_reduce_volume,
                    sessions,
                    count,
                    stringResource(mainMuscle.listContinuationStringRes),
                )
            } else {
                stringResource(
                    R.string.load_suggestion_stalled_reduce_volume_unnamed,
                    sessions,
                    count,
                )
            }
        StallEscalation.ChangeExercise ->
            stringResource(R.string.load_suggestion_stalled_change_exercise, sessions, count)
    }
}

@Composable
private fun weight(value: Double, massUnit: MassUnit): String =
    LocalFormatter.current.formatWeight(value, massUnit)

@Composable private fun reps(count: Int): String = pluralStringResource(R.plurals.rep_count, count)
