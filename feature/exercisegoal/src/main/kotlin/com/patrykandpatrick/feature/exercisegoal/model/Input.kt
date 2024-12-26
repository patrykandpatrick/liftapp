package com.patrykandpatrick.feature.exercisegoal.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit

@Stable
sealed interface Input<T : Any> {
    val state: TextFieldState<T>
    val labelRes: Int
    val valueUnit: ValueUnit?
        get() = null

    data class MinReps(override val state: TextFieldState<Int>) : Input<Int> {
        override val labelRes = R.string.goal_min_reps
    }

    data class MaxReps(override val state: TextFieldState<Int>) : Input<Int> {
        override val labelRes = R.string.goal_max_reps
    }

    data class Sets(override val state: TextFieldState<Int>) : Input<Int> {
        override val labelRes = R.string.goal_sets
    }

    data class RestTime(override val state: TextFieldState<Long>) : Input<Long> {
        override val labelRes = R.string.goal_rest_time
    }

    data class Distance(override val state: TextFieldState<Double>, val unit: LongDistanceUnit) :
        Input<Double> {
        override val labelRes = R.string.goal_distance
        override val valueUnit = unit
    }

    data class Duration(override val state: TextFieldState<Long>) : Input<Long> {
        override val labelRes = R.string.goal_duration
    }

    data class Calories(override val state: TextFieldState<Double>) : Input<Double> {
        override val labelRes = R.string.goal_calories
    }
}
