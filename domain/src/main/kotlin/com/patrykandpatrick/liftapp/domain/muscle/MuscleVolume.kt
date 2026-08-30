package com.patrykandpatrick.liftapp.domain.muscle

import com.patrykandpatrick.liftapp.domain.date.DAYS_IN_WEEK
import kotlin.math.roundToInt

/**
 * Working sets credited to each muscle over a window of training, discounted by tier: [DIRECT] for
 * a primary muscle, [INDIRECT] for a secondary, [MINOR] for a tertiary.
 *
 * The fractions are a convention, not a measurement, so only [wholeSetsOf] and [bandOf] are fit to
 * show a user; [get] is for the next fold.
 */
@JvmInline
value class MuscleVolume private constructor(val setsPerMuscle: Map<Muscle, Double>) {

    val isEmpty: Boolean
        get() = setsPerMuscle.isEmpty()

    val workedMuscles: List<Muscle>
        get() =
            setsPerMuscle
                .asSequence()
                .filter { (_, sets) -> sets > 0 }
                .sortedWith(
                    compareByDescending<Map.Entry<Muscle, Double>> { it.value }.thenBy { it.key }
                )
                .map { (muscle, _) -> muscle }
                .toList()

    operator fun get(muscle: Muscle): Double = setsPerMuscle[muscle] ?: 0.0

    /** A quarter of a set is an artefact of the fractional credits, not a set anybody did. */
    fun wholeSetsOf(muscle: Muscle): Int = this[muscle].roundToInt()

    /** Read off [wholeSetsOf] so the band can never contradict the count shown beside it. */
    fun bandOf(muscle: Muscle): VolumeBand = VolumeBand.of(wholeSetsOf(muscle).toDouble())

    /**
     * Restated as a weekly rate, the only rate the volume landmarks are given in. [days] must be
     * the days that have *elapsed* — see `DateInterval.elapsedDays`.
     *
     * Never scales up: a shorter window is a count that happened, where the week it implies is a
     * guess.
     */
    fun perWeek(days: Long): MuscleVolume {
        require(days > 0) { "A window has to span at least a day, not $days." }
        if (days <= DAYS_IN_WEEK || setsPerMuscle.isEmpty()) return this
        val weeks = days.toDouble() / DAYS_IN_WEEK
        return MuscleVolume(setsPerMuscle.mapValues { (_, sets) -> sets / weeks })
    }

    operator fun plus(other: MuscleVolume): MuscleVolume =
        when {
            setsPerMuscle.isEmpty() -> other
            other.setsPerMuscle.isEmpty() -> this
            else ->
                MuscleVolume(
                    buildMap(setsPerMuscle.size + other.setsPerMuscle.size) {
                        putAll(setsPerMuscle)
                        other.setsPerMuscle.forEach { (muscle, sets) ->
                            merge(muscle, sets, Double::plus)
                        }
                    }
                )
        }

    companion object {
        val Empty = MuscleVolume(emptyMap())

        const val DIRECT = 1.0
        const val INDIRECT = 0.5
        const val MINOR = 0.25

        /**
         * A muscle named in more than one tier is credited once, at its highest: the lists are
         * hand-authored and a repeat is a slip, not one and a half times the work.
         */
        fun of(
            sets: Int,
            primaryMuscles: Collection<Muscle>,
            secondaryMuscles: Collection<Muscle>,
            tertiaryMuscles: Collection<Muscle>,
        ): MuscleVolume {
            if (sets <= 0) return Empty
            val setCount = sets.toDouble()
            return MuscleVolume(
                buildMap {
                    tertiaryMuscles.forEach { muscle -> put(muscle, setCount * MINOR) }
                    secondaryMuscles.forEach { muscle -> put(muscle, setCount * INDIRECT) }
                    primaryMuscles.forEach { muscle -> put(muscle, setCount * DIRECT) }
                }
            )
        }

        fun of(sets: Int, muscles: MuscleContainer): MuscleVolume =
            of(
                sets = sets,
                primaryMuscles = muscles.primaryMuscles,
                secondaryMuscles = muscles.secondaryMuscles,
                tertiaryMuscles = muscles.tertiaryMuscles,
            )
    }
}
