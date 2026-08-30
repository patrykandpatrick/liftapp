package com.patrykandpatrick.liftapp.domain.workout

import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import java.io.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class ExerciseSet : Serializable {
    abstract val isCompleted: Boolean
    abstract val notes: String
    open val weight: Double? = null
    open val weightUnit: MassUnit? = null
    open val reps: Int? = null
    open val duration: Duration? = null
    open val distance: Double? = null
    open val distanceUnit: LongDistanceUnit? = null
    open val kcal: Double? = null

    /**
     * The mass the set moved: for [Calisthenics] the body weight as well as whatever was added.
     * Every reading of a set's load goes through this rather than [weight], so the layers agree on
     * what a bodyweight pull-up counted as.
     */
    open val totalWeight: Double?
        get() = weight

    /**
     * Reps in reserve: how many more reps the user could have done, within [RIR_RANGE]. `null`
     * means they did not report it, which is not the same as `0` — zero is a set taken to failure.
     * Meaningless for [Cardio] and [Time], which leave it `null` always.
     */
    open val rir: Int? = null

    open class Weight(
        override val weight: Double,
        override val reps: Int,
        override val weightUnit: MassUnit,
        override val notes: String = "",
        override val rir: Int? = null,
    ) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = weight > 0 && reps > 0

        override val totalWeight: Double
            get() = weight

        companion object {
            fun empty(massUnit: MassUnit) = Weight(0.0, 0, massUnit)
        }

        override fun toString() =
            "Weight(weight=$weight, reps=$reps, weightUnit=$weightUnit, rir=$rir)"

        override fun equals(other: Any?) =
            other is Weight &&
                weight == other.weight &&
                reps == other.reps &&
                weightUnit == other.weightUnit &&
                notes == other.notes &&
                rir == other.rir

        override fun hashCode() =
            weight.hashCode() * 31 +
                reps.hashCode() * 31 +
                weightUnit.hashCode() * 31 +
                notes.hashCode() * 31 +
                rir.hashCode()
    }

    open class Calisthenics(
        override val weight: Double,
        open val bodyWeight: Double,
        override val reps: Int,
        override val weightUnit: MassUnit,
        override val notes: String = "",
        override val rir: Int? = null,
    ) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = reps > 0

        override val totalWeight: Double
            get() = weight + bodyWeight

        companion object {
            fun empty(bodyWeight: Double, massUnit: MassUnit) =
                Calisthenics(0.0, bodyWeight, 0, massUnit)
        }

        override fun toString() =
            "Calisthenics(weight=$weight, bodyWeight=$bodyWeight, reps=$reps, " +
                "weightUnit=$weightUnit, rir=$rir)"

        override fun equals(other: Any?) =
            other is Calisthenics &&
                weight == other.weight &&
                bodyWeight == other.bodyWeight &&
                reps == other.reps &&
                weightUnit == other.weightUnit &&
                notes == other.notes &&
                rir == other.rir

        override fun hashCode() =
            weight.hashCode() * 31 +
                bodyWeight.hashCode() * 31 +
                reps.hashCode() * 31 +
                weightUnit.hashCode() * 31 +
                notes.hashCode() * 31 +
                rir.hashCode()
    }

    open class Reps(
        override val reps: Int,
        override val notes: String = "",
        override val rir: Int? = null,
    ) : ExerciseSet() {
        override val isCompleted: Boolean
            get() = reps > 0

        companion object {
            val empty = Reps(0)
        }

        override fun toString() = "Reps(reps=$reps, rir=$rir)"

        override fun equals(other: Any?) =
            other is Reps && reps == other.reps && notes == other.notes && rir == other.rir

        override fun hashCode() = reps.hashCode() * 31 + notes.hashCode() * 31 + rir.hashCode()
    }

    open class Cardio(
        override val duration: Duration,
        override val distance: Double,
        override val kcal: Double,
        override val distanceUnit: LongDistanceUnit,
        override val notes: String = "",
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
                distanceUnit == other.distanceUnit &&
                notes == other.notes

        override fun hashCode() =
            duration.hashCode() * 31 +
                distance.hashCode() * 31 +
                kcal.hashCode() * 31 +
                distanceUnit.hashCode() * 31 +
                notes.hashCode()
    }

    open class Time(override val duration: Duration, override val notes: String = "") :
        ExerciseSet() {
        override val isCompleted: Boolean
            get() = duration.inWholeSeconds > 0

        companion object {
            val empty = Time(0.seconds)
        }

        override fun toString() = "Time(duration=$duration)"

        override fun equals(other: Any?) =
            other is Time && duration == other.duration && notes == other.notes

        override fun hashCode() = duration.hashCode() * 31 + notes.hashCode()
    }

    companion object {
        /**
         * The reportable range of [rir]. Lifters cannot tell one value from the next much past four
         * reps from failure, so the top of the range is an open bucket — "four or more" — rather
         * than an exact count. Everything that can put a value into [rir] clamps to this, the
         * restore path included, and the top reads as a bucket wherever it is displayed.
         */
        val RIR_RANGE = 0..4
    }
}
