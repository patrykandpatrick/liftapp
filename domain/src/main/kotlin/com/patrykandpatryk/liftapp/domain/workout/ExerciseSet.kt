package com.patrykandpatryk.liftapp.domain.workout

import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import java.io.Serializable

sealed class ExerciseSet : Serializable {
    abstract val isComplete: Boolean
    open val weight: Double? = null
    open val weightUnit: MassUnit? = null
    open val reps: Int? = null
    open val timeMillis: Long? = null
    open val distance: Double? = null
    open val distanceUnit: LongDistanceUnit? = null
    open val kcal: Double? = null

    data class Weight(
        override val weight: Double,
        override val reps: Int,
        override val weightUnit: MassUnit
    ): ExerciseSet() {
        override val isComplete: Boolean
            get() = weight > 0 && reps > 0

        companion object {
            fun empty(massUnit: MassUnit) = Weight(0.0, 0, massUnit)
        }
    }

    data class Calisthenics(
        override val weight: Double,
        val bodyWeight: Double, // TODO add body weight
        override val reps: Int,
        override val weightUnit: MassUnit,
    ): ExerciseSet() {
        override val isComplete: Boolean
            get() = weight > 0 && reps > 0

        companion object {
            fun empty(massUnit: MassUnit) = Calisthenics(0.0, 0.0, 0, massUnit)
        }
    }

    data class Reps(
        override val reps: Int,
    ): ExerciseSet() {
        override val isComplete: Boolean
            get() = reps > 0

        companion object {
            val empty = Reps(0)
        }
    }

    data class Cardio(
        override val timeMillis: Long,
        override val distance: Double,
        override val kcal: Double,
        override val distanceUnit: LongDistanceUnit,
    ): ExerciseSet() {
        override val isComplete: Boolean
            get() = timeMillis > 0 && distance > 0

        companion object {
            fun empty(distanceUnit: LongDistanceUnit) = Cardio(0, 0.0, 0.0, distanceUnit)
        }
    }

    data class Time(
        override val timeMillis: Long,
    ): ExerciseSet() {
        override val isComplete: Boolean
            get() = timeMillis > 0

        companion object {
            val empty = Time(0)
        }
    }
}