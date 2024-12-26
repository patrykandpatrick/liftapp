package com.patrykandpatrick.feature.exercisegoal.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.type.isAnyOf
import com.patrykandpatryk.liftapp.domain.validation.isHigherOrEqualTo
import com.patrykandpatryk.liftapp.domain.validation.validNumber
import com.patrykandpatryk.liftapp.domain.validation.valueInRange

@Stable
data class GoalInput(
    val minReps: Input.MinReps?,
    val maxReps: Input.MaxReps?,
    val sets: Input.Sets?,
    val restTime: Input.RestTime,
    val distance: Input.Distance?,
    val duration: Input.Duration?,
    val calories: Input.Calories?,
) {
    companion object {
        fun create(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
            type: ExerciseType,
        ): GoalInput {
            val minReps =
                if (
                    type.isAnyOf(ExerciseType.Reps, ExerciseType.Weight, ExerciseType.Calisthenics)
                ) {
                    Input.MinReps(
                        textFieldStateManager
                            .intTextField(
                                validators = {
                                    validNumber()
                                    valueInRange(Goal.RepRange)
                                }
                            )
                            .apply { updateValue(goal.minReps) }
                    )
                } else {
                    null
                }

            val maxReps =
                if (minReps != null) {
                    Input.MaxReps(
                        textFieldStateManager
                            .intTextField(
                                validators = {
                                    validNumber()
                                    valueInRange(Goal.RepRange)
                                    isHigherOrEqualTo(minReps.state)
                                }
                            )
                            .apply { updateValue(goal.maxReps) }
                    )
                } else {
                    null
                }

            val sets =
                Input.Sets(
                    textFieldStateManager
                        .intTextField(
                            validators = {
                                validNumber()
                                valueInRange(Goal.SetRange)
                            }
                        )
                        .apply { updateValue(goal.sets) }
                )

            val restTime =
                Input.RestTime(
                    textFieldStateManager.longTextField(validators = { validNumber() }).apply {
                        updateValue(goal.breakDuration.inWholeMilliseconds)
                    }
                )

            return GoalInput(minReps, maxReps, sets, restTime, null, null, null)
        }
    }
}
