package com.patrykandpatrick.liftapp.feature.plan.targets.ui

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets

@Stable data class ScreenState(val day: PlanDayTargets, val editor: Editor?)

/**
 * One exercise's prescription, open for editing.
 *
 * The fields are the exercise's, not one set's: a rep range and an effort target that every set of
 * it shares. That is the shape the generator writes, and the shape a lifter changes — "make this
 * exercise fives this week", not "make set three fives". Saving therefore levels any difference
 * between the sets, which nothing can author yet.
 */
@Stable
class Editor(
    val name: Name,
    val exerciseType: ExerciseType,
    val setCount: IntTextFieldState,
    val minReps: IntTextFieldState,
    val maxReps: IntTextFieldState,
    val restSeconds: IntTextFieldState,
    val rir: Int?,
)
