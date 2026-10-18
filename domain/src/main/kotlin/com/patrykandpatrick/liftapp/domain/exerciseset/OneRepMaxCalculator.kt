package com.patrykandpatrick.liftapp.domain.exerciseset

import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

/**
 * Estimates a one-rep max from a submaximal set, and inverts that estimate to get a working load
 * for a rep and RIR target.
 *
 * Two curves, chosen by how heavy the set was. At or above [WEIGHT_DEPENDENT_FLOOR_KG] the
 * weight-dependent curve derived from 303,494 near-failure sets across 388 exercises ([arXiv
 * 2603.17495](https://arxiv.org/abs/2603.17495)) applies:
 * ```
 * 1RM = w × (1 + (r - 1)^0.85 / (-2.55 + 4.58 × ln w))
 * ```
 *
 * It lets the rep-to-1RM conversion vary with the load — a 20 kg lateral raise and a 200 kg
 * deadlift genuinely do not share one — and is exact at one rep, where Epley returns 96.8% of the
 * load. Below the floor it collapses and Epley continues to serve.
 *
 * **`w` is in kilograms.** The `ln w` term makes the curve unit-dependent, so every entry point
 * takes the unit its numbers are in and normalises before applying it. Epley is scale-invariant, so
 * nothing needed a unit until this curve arrived.
 */
object OneRepMaxCalculator {
    private const val EPLEY_REP_COUNT_DIVISOR = 30.0
    private const val REP_EXPONENT = 0.85
    private const val DIVISOR_INTERCEPT = -2.55
    private const val DIVISOR_LOG_COEFFICIENT = 4.58

    private const val SOLVER_TOLERANCE_KG = 1e-9
    private const val REFERENCE_ONE_REP_MAX_KG = 100.0
    private const val SOLVER_MAX_ITERATIONS = 32

    /**
     * The lightest load the weight-dependent curve is applied to.
     *
     * Below it the `ln w` term takes over: the divisor reaches zero at 1.745 kg and the estimate
     * runs away long before that. A 10 kg lateral raise for 12 reps comes out as 51% of one-rep max
     * against a table value of 69% — precisely the light-load, high-rep isolation work the app
     * prescribes most of.
     *
     * 20 kg is an equipment landmark — a bare barbell, the top of the usual dumbbell isolation
     * range — rather than a fitted value, because the curve degrades continuously rather than at a
     * boundary. It is low enough that the whole of the reference's percentage table, whose lightest
     * cell is 53 kg, remains the weight-dependent curve's own output, and high enough to keep the
     * collapse out of the answer.
     *
     * The two curves do not meet — they cross near 100 kg — so this is a step rather than a seam.
     * The step is upward for every rep count, so the estimate still rises with the load across it.
     */
    internal const val WEIGHT_DEPENDENT_FLOOR_KG = 20.0

    /**
     * The one-rep max implied by [reps] reps at [mass], taking the set as having gone to failure.
     * Where an RIR was recorded, use [estimatedOneRepMax] instead: a set of 8 at 2 RIR is a 10-rep
     * max, and reading it as an 8-rep max under-reports the lifter.
     *
     * [mass] is in [unit], and so is the result.
     */
    fun getOneRepMax(mass: Double, reps: Int, unit: MassUnit): Double {
        if (reps <= 0 || mass <= 0.0) return 0.0
        if (reps == 1) return mass
        val kilograms = unit.toKilograms(mass)
        val oneRepMax =
            if (kilograms >= WEIGHT_DEPENDENT_FLOOR_KG) {
                kilograms * (1.0 + repFactor(reps) / divisor(kilograms))
            } else {
                kilograms * epleyFactor(reps)
            }
        return unit.fromKilograms(oneRepMax)
    }

    /**
     * The one-rep max implied by [reps] reps at [mass] stopped [rir] reps short of failure, which
     * is the same thing as a set of `reps + rir` taken to failure.
     *
     * [rir] is not nullable on purpose. An unrecorded RIR is not a zero, and a caller holding a
     * null has to decide whether the set is worth reading at all — [getOneRepMax] is the answer
     * only where the set really was taken to failure.
     *
     * [mass] is in [unit], and so is the result.
     */
    fun estimatedOneRepMax(mass: Double, reps: Int, rir: Int, unit: MassUnit): Double =
        if (reps <= 0) 0.0 else getOneRepMax(mass, reps + rir.coerceAtLeast(0), unit)

    /**
     * The load that puts [reps] reps within [rir] of failure, given a [oneRepMax]. The inverse of
     * [estimatedOneRepMax], and what generates the reference's percentage table.
     *
     * The weight-dependent curve has no closed-form inverse, so this iterates. Where the answer
     * falls below [WEIGHT_DEPENDENT_FLOOR_KG] it comes from Epley, matching [getOneRepMax]; the two
     * curves leave a narrow band of one-rep maxes that neither reaches, and a target in that band
     * returns the floor itself.
     *
     * [oneRepMax] is in [unit], and so is the result. Snap it to the equipment with
     * [roundToIncrement] before showing it to anyone.
     */
    fun loadFor(oneRepMax: Double, reps: Int, rir: Int, unit: MassUnit): Double {
        if (reps <= 0 || oneRepMax <= 0.0) return 0.0
        val effectiveReps = reps + rir.coerceAtLeast(0)
        if (effectiveReps == 1) return oneRepMax
        val kilograms = unit.toKilograms(oneRepMax)
        val epleyLoad = kilograms / epleyFactor(effectiveReps)
        val load =
            if (epleyLoad < WEIGHT_DEPENDENT_FLOOR_KG) {
                // The weight-dependent curve always needs less load than Epley for the same one-rep
                // max, so an Epley answer under the floor settles it: the real one is under it too.
                epleyLoad
            } else {
                solveWeightDependentLoad(kilograms, effectiveReps)
                    .coerceAtLeast(WEIGHT_DEPENDENT_FLOOR_KG)
            }
        return unit.fromKilograms(load)
    }

    /**
     * [load] snapped to the nearest multiple of [increment] — "a suggestion of 62.4 kg is worse
     * than 62.5 kg even though it is more precise". Ties round up.
     *
     * Both values are in the same unit, and this one does not normalise: note that
     * `Exercise.loadIncrement` is stored in kilograms. A null or non-positive [increment] — an
     * exercise whose equipment step nobody has recorded — leaves [load] alone rather than inventing
     * a step.
     */
    fun roundToIncrement(load: Double, increment: Double?): Double =
        if (increment == null || increment <= 0.0) load
        else floor(load / increment + 0.5) * increment

    /**
     * The percentage of a one-rep max that puts [reps] reps within [rir] of failure, read at a 100
     * kg one-rep max — the convention the reference's percentage table is printed in. The curve is
     * weight-dependent, so this is a starting figure for a plan rather than a law for every lift.
     */
    fun percentOfOneRepMax(reps: Int, rir: Int): Double =
        loadFor(REFERENCE_ONE_REP_MAX_KG, reps, rir, MassUnit.Kilograms) /
            REFERENCE_ONE_REP_MAX_KG * 100.0

    /**
     * Solves `load × (1 + (r - 1)^0.85 / (-2.55 + 4.58 × ln load)) = oneRepMaxKilograms` for
     * `load`.
     *
     * Iterating `load ← oneRepMax / (1 + k / divisor(load))` from the one-rep max descends onto the
     * answer monotonically — the map is increasing and the starting point is an over-estimate — and
     * settles within a handful of steps. It stops as soon as an iterate falls below
     * [WEIGHT_DEPENDENT_FLOOR_KG], both because the answer is then outside this curve's range and
     * because evaluating `ln` further down is what the floor exists to avoid.
     */
    private fun solveWeightDependentLoad(oneRepMaxKilograms: Double, reps: Int): Double {
        val repFactor = repFactor(reps)
        var load = oneRepMaxKilograms
        var iterations = 0
        var settled = false
        while (!settled && iterations < SOLVER_MAX_ITERATIONS) {
            val next = oneRepMaxKilograms / (1.0 + repFactor / divisor(load))
            settled = abs(next - load) < SOLVER_TOLERANCE_KG || next < WEIGHT_DEPENDENT_FLOOR_KG
            load = next
            iterations++
        }
        return load
    }

    private fun epleyFactor(reps: Int): Double = 1.0 + reps / EPLEY_REP_COUNT_DIVISOR

    private fun repFactor(reps: Int): Double = (reps - 1).toDouble().pow(REP_EXPONENT)

    private fun divisor(kilograms: Double): Double =
        DIVISOR_INTERCEPT + DIVISOR_LOG_COEFFICIENT * ln(kilograms)
}
