package com.patrykandpatrick.feature.exercisegoal.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.type.isAnyOf
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.validation.isHigherOrEqualTo
import com.patrykandpatryk.liftapp.domain.validation.validNumber
import com.patrykandpatryk.liftapp.domain.validation.valueInRange

@Stable
data class GoalInput(
    val minReps: Input.MinReps?,
    val maxReps: Input.MaxReps?,
    val sets: Input.Sets?,
    val restTime: Input.RestTime,
    val duration: Input.Duration?,
    val distance: Input.Distance?,
    val calories: Input.Calories?,
) {
    val inputs = listOfNotNull(minReps, maxReps, sets, duration, distance, calories)

    fun isLastInput(input: Input<*>): Boolean = inputs.last() == input

    companion object {
        fun create(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
            type: ExerciseType,
            longDistanceUnit: LongDistanceUnit,
        ): GoalInput {
            val minReps =
                if (
                    type.isAnyOf(ExerciseType.Reps, ExerciseType.Weight, ExerciseType.Calisthenics)
                ) {
                    getMinRepsInput(textFieldStateManager, goal)
                } else {
                    null
                }

            val maxReps =
                if (minReps != null) getMaxRepsInput(textFieldStateManager, minReps, goal) else null

            val sets = getSetsInput(textFieldStateManager, goal)

            val restTime = getRestTimeInput(textFieldStateManager, goal)

            val duration =
                if (type.isAnyOf(ExerciseType.Time, ExerciseType.Cardio)) {
                    getDurationTimeInput(textFieldStateManager, goal)
                } else {
                    null
                }

            val distance =
                if (type == ExerciseType.Cardio) {
                    getDistanceInput(textFieldStateManager, goal, longDistanceUnit)
                } else {
                    null
                }

            val calories =
                if (type == ExerciseType.Cardio) {
                    getCaloriesInput(textFieldStateManager, goal)
                } else {
                    null
                }

            return GoalInput(minReps, maxReps, sets, restTime, duration, distance, calories)
        }

        private fun getMinRepsInput(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
        ): Input.MinReps =
            Input.MinReps(
                textFieldStateManager
                    .intTextField(
                        validators = {
                            validNumber()
                            valueInRange(Goal.repRange)
                        }
                    )
                    .apply { updateValue(goal.minReps) }
            )

        private fun getMaxRepsInput(
            textFieldStateManager: TextFieldStateManager,
            minReps: Input.MinReps,
            goal: Goal,
        ): Input.MaxReps =
            Input.MaxReps(
                textFieldStateManager
                    .intTextField(
                        validators = {
                            validNumber()
                            valueInRange(Goal.repRange)
                            isHigherOrEqualTo(minReps.state)
                        }
                    )
                    .apply { updateValue(goal.maxReps) }
            )

        private fun getSetsInput(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
        ): Input.Sets =
            Input.Sets(
                textFieldStateManager
                    .intTextField(
                        validators = {
                            validNumber()
                            valueInRange(Goal.setRange)
                        }
                    )
                    .apply { updateValue(goal.sets) }
            )

        private fun getRestTimeInput(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
        ): Input.RestTime =
            Input.RestTime(
                textFieldStateManager
                    .longTextField(
                        validators = {
                            validNumber()
                            valueInRange(0.0)
                        }
                    )
                    .apply { updateValue(goal.restTime.inWholeMilliseconds) }
            )

        private fun getDurationTimeInput(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
        ): Input.Duration =
            Input.Duration(
                textFieldStateManager
                    .longTextField(
                        validators = {
                            validNumber()
                            valueInRange(0.0)
                        }
                    )
                    .apply { updateValue(goal.duration.inWholeMilliseconds) }
            )

        private fun getDistanceInput(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
            unit: LongDistanceUnit,
        ): Input.Distance =
            Input.Distance(
                textFieldStateManager
                    .doubleTextField(
                        validators = {
                            validNumber()
                            valueInRange(0.0)
                        }
                    )
                    .apply { updateValue(goal.distanceUnit.convert(goal.distance, unit)) },
                goal.distanceUnit,
            )

        private fun getCaloriesInput(
            textFieldStateManager: TextFieldStateManager,
            goal: Goal,
        ): Input.Calories =
            Input.Calories(
                textFieldStateManager
                    .doubleTextField(
                        validators = {
                            validNumber()
                            valueInRange(0.0)
                        }
                    )
                    .apply { updateValue(goal.calories) }
            )
    }
}
