package com.patrykandpatryk.liftapp.domain.workout

import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import java.io.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class ExerciseSet : Serializable {
    abstract val isCompleted: Boolean
    open val weight: Double? = null
    open val weightUnit: MassUnit? = null
    open val reps: Int? = null
    open val duration: Duration? = null
    open val distance: Double? = null
    open val distanceUnit: LongDistanceUnit? = null
    open val kcal: Double? = null

    open class Weight(
        override val weight: Double,
        override val reps: Int,
        override val weightUnit: MassUnit,
    ) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = weight > 0 && reps > 0

        companion object {
            fun empty(massUnit: MassUnit) = Weight(0.0, 0, massUnit)
        }

        override fun toString() = "Weight(weight=$weight, reps=$reps, weightUnit=$weightUnit)"

        override fun equals(other: Any?) =
            other is Weight &&
                weight == other.weight &&
                reps == other.reps &&
                weightUnit == other.weightUnit

        override fun hashCode() =
            weight.hashCode() * 31 + reps.hashCode() * 31 + weightUnit.hashCode()
    }

    open class Calisthenics(
        override val weight: Double,
        open val bodyWeight: Double,
        override val reps: Int,
        override val weightUnit: MassUnit,
    ) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = reps > 0

        companion object {
            fun empty(bodyWeight: Double, massUnit: MassUnit) =
                Calisthenics(0.0, bodyWeight, 0, massUnit)
        }

        override fun toString() =
            "Calisthenics(weight=$weight, bodyWeight=$bodyWeight, reps=$reps, weightUnit=$weightUnit)"

        override fun equals(other: Any?) =
            other is Calisthenics &&
                weight == other.weight &&
                bodyWeight == other.bodyWeight &&
                reps == other.reps &&
                weightUnit == other.weightUnit

        override fun hashCode() =
            weight.hashCode() * 31 +
                bodyWeight.hashCode() * 31 +
                reps.hashCode() * 31 +
                weightUnit.hashCode()
    }

    open class Reps(override val reps: Int) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = reps > 0

        companion object {
            val empty = Reps(0)
        }

        override fun toString() = "Reps(reps=$reps)"

        override fun equals(other: Any?) = other is Reps && reps == other.reps

        override fun hashCode() = reps.hashCode()
    }

    open class Cardio(
        override val duration: Duration,
        override val distance: Double,
        override val kcal: Double,
        override val distanceUnit: LongDistanceUnit,
    ) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = duration.inWholeSeconds > 0 && distance > 0

        companion object {
            fun empty(distanceUnit: LongDistanceUnit) = Cardio(0.seconds, 0.0, 0.0, distanceUnit)
        }

        override fun toString() =
            "Cardio(duration=$duration, distance=$distance, kcal=$kcal, distanceUnit=$distanceUnit)"

        override fun equals(other: Any?) =
            other is Cardio &&
                duration == other.duration &&
                distance == other.distance &&
                kcal == other.kcal &&
                distanceUnit == other.distanceUnit

        override fun hashCode() =
            duration.hashCode() * 31 +
                distance.hashCode() * 31 +
                kcal.hashCode() * 31 +
                distanceUnit.hashCode()
    }

    open class Time(override val duration: Duration) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = duration.inWholeSeconds > 0

        companion object {
            val empty = Time(0.seconds)
        }

        override fun toString() = "Time(duration=$duration)"

        override fun equals(other: Any?) = other is Time && duration == other.duration

        override fun hashCode() = duration.hashCode()
    }
}
