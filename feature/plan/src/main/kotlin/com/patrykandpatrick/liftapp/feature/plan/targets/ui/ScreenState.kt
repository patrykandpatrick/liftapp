package com.patrykandpatrick.liftapp.feature.plan.targets.ui

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.feature.plan.targets.model.TargetDraft
import kotlin.math.roundToInt

@Stable
data class ScreenState(val day: PlanDayTargets, val editor: Editor?) {
    val hasPreviousCycle: Boolean
        get() = day.cycleIndex > 0

    val hasNextCycle: Boolean
        get() = day.cycleIndex < day.cycleCount - 1
}

/**
 * One exercise's prescription, open for editing.
 *
 * The set count and the rest are the exercise's. Everything else is a set's: the [draft]'s selected
 * one, or every set at once when none is selected, which is how a lifter says "make this exercise
 * fives this week" without saying it set by set.
 */
@Stable
class Editor(
    val name: Name,
    val exerciseType: ExerciseType,
    val draft: TargetDraft,
    val setCount: IntTextFieldState,
    val minReps: IntTextFieldState,
    val maxReps: IntTextFieldState,
    val loadPercent: IntTextFieldState,
    val restSeconds: IntTextFieldState,
) {
    val rir: Int?
        get() = draft.valueOf(SetTarget::rir)

    /**
     * A percentage of one-rep max is a load on a bar. A bodyweight lift's would be a share of the
     * whole mass moved, which is not what its user sets.
     */
    val hasLoadPercent: Boolean
        get() = exerciseType == ExerciseType.Weight

    /**
     * What the one-rep max curve says the shown set's reps and effort call for, as a starting
     * figure to accept or ignore. Read off the bottom of the range, the same rep count a load-up
     * aims at; an open effort target is read as to failure.
     */
    val suggestedLoadPercent: Int?
        get() {
            if (!hasLoadPercent || draft.varies(SetTarget::rir)) return null
            val reps = draft.valueOf(SetTarget::minReps)?.takeIf { it > 0 } ?: return null
            return OneRepMaxCalculator.percentOfOneRepMax(reps, rir ?: 0).roundToInt()
        }

    /** What the Save button is gated on: a target of no sets leaves the workout nothing to log. */
    val isValid: Boolean
        get() = setCount.isValid && minReps.isValid && maxReps.isValid && restSeconds.isValid
}
