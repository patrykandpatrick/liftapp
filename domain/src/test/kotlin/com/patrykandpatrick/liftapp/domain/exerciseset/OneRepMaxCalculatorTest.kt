package com.patrykandpatrick.liftapp.domain.exerciseset

import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator.WEIGHT_DEPENDENT_FLOOR_KG
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import kotlin.math.roundToInt
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.Test

/**
 * The reference table in `docs/workout-programming.md` — "Load for a target" — is the acceptance
 * test, and it is the *inverse* curve: each cell is `loadFor` against a 100 kg one-rep max, not the
 * percentage a 100 kg set implies. Reading it forwards misses by up to 3.3 points at 20 reps,
 * because the curve is weight-dependent and the load being solved for is not 100 kg.
 */
class OneRepMaxCalculatorTest {

    private val kg = MassUnit.Kilograms

    private val lb = MassUnit.Pounds

    /** `reps` to the percentage of one-rep max at 0, 1, 2 and 3 RIR. */
    private val referenceLoadTable =
        mapOf(
            1 to listOf(100, 95, 91, 88),
            3 to listOf(91, 88, 85, 82),
            5 to listOf(85, 82, 79, 77),
            6 to listOf(82, 79, 77, 75),
            8 to listOf(77, 75, 73, 71),
            10 to listOf(73, 71, 69, 67),
            12 to listOf(69, 67, 65, 64),
            15 to listOf(64, 62, 61, 59),
            20 to listOf(57, 55, 54, 53),
        )

    @Test
    fun `Every cell of the reference load table is reproduced`() {
        referenceLoadTable.forEach { (reps, percentages) ->
            percentages.forEachIndexed { rir, percentage ->
                val load = OneRepMaxCalculator.loadFor(REFERENCE_ONE_REP_MAX, reps, rir, kg)
                assertEquals(
                    percentage,
                    load.roundToInt(),
                    "$reps reps at $rir RIR should be $percentage% of a " +
                        "${REFERENCE_ONE_REP_MAX.toInt()} kg one-rep max, was $load kg",
                )
            }
        }
    }

    @Test
    fun `The percentage offered for a target is the reference table's`() {
        referenceLoadTable.forEach { (reps, percentages) ->
            percentages.forEachIndexed { rir, percentage ->
                assertEquals(
                    percentage,
                    OneRepMaxCalculator.percentOfOneRepMax(reps, rir).roundToInt(),
                    "$reps reps at $rir RIR",
                )
            }
        }
    }

    @Test
    fun `The whole reference table sits above the weight-dependent floor`() {
        // If it did not, the acceptance test above would be measuring Epley in places.
        val lightest = referenceLoadTable.values.flatten().min() * REFERENCE_ONE_REP_MAX / PERCENT
        assertTrue(
            lightest > WEIGHT_DEPENDENT_FLOOR_KG,
            "lightest cell $lightest kg is not above the $WEIGHT_DEPENDENT_FLOOR_KG kg floor",
        )
    }

    @Test
    fun `A single rep is the load itself, which is where Epley is wrong`() {
        val load = 142.5
        assertEquals(load, OneRepMaxCalculator.getOneRepMax(load, reps = 1, unit = kg))
        assertEquals(load, OneRepMaxCalculator.loadFor(load, reps = 1, rir = 0, unit = kg))
        // Epley would put this at 96.8% of the load.
        assertNotEquals(load, load * (1.0 + 1.0 / 30.0))
    }

    @Test
    fun `RIR shifts the estimate along the rep axis`() {
        val load = 100.0
        assertEquals(
            OneRepMaxCalculator.getOneRepMax(load, reps = 10, unit = kg),
            OneRepMaxCalculator.estimatedOneRepMax(load, reps = 8, rir = 2, unit = kg),
        )
        assertEquals(
            OneRepMaxCalculator.getOneRepMax(load, reps = 8, unit = kg),
            OneRepMaxCalculator.estimatedOneRepMax(load, reps = 8, rir = 0, unit = kg),
        )
        // A set stopped short is worth more than the same set read as having gone to failure.
        assertTrue(
            OneRepMaxCalculator.estimatedOneRepMax(load, reps = 8, rir = 3, unit = kg) >
                OneRepMaxCalculator.estimatedOneRepMax(load, reps = 8, rir = 0, unit = kg)
        )
    }

    @Test
    fun `loadFor inverts the estimate`() {
        listOf(25.0, 60.0, 100.0, 142.5, 300.0).forEach { load ->
            (1..20).forEach { reps ->
                REPORTABLE_RIR.forEach { rir ->
                    val oneRepMax = OneRepMaxCalculator.estimatedOneRepMax(load, reps, rir, kg)
                    assertEquals(
                        load,
                        OneRepMaxCalculator.loadFor(oneRepMax, reps, rir, kg),
                        TOLERANCE_KG,
                        "$load kg × $reps at $rir RIR did not survive the round trip",
                    )
                }
            }
        }
    }

    @Test
    fun `The estimate is never below the load itself`() {
        listOf(1.0, 2.0, 5.0, 10.0, WEIGHT_DEPENDENT_FLOOR_KG, 20.5, 60.0, 200.0).forEach { load ->
            (1..30).forEach { reps ->
                val oneRepMax = OneRepMaxCalculator.getOneRepMax(load, reps, kg)
                assertTrue(
                    oneRepMax >= load,
                    "$load kg × $reps estimated a $oneRepMax kg one-rep max",
                )
            }
        }
    }

    @Test
    fun `Below the floor the estimate is Epley`() {
        listOf(2.0, 5.0, 10.0, 19.9).forEach { load ->
            (2..20).forEach { reps ->
                assertEquals(
                    load * (1.0 + reps / 30.0),
                    OneRepMaxCalculator.getOneRepMax(load, reps, kg),
                    TOLERANCE_KG,
                    "$load kg × $reps should have fallen back to Epley",
                )
            }
        }
    }

    @Test
    fun `At and above the floor the estimate is the weight-dependent curve`() {
        listOf(WEIGHT_DEPENDENT_FLOOR_KG, 60.0, 100.0, 200.0).forEach { load ->
            (2..20).forEach { reps ->
                assertNotEquals(
                    load * (1.0 + reps / 30.0),
                    OneRepMaxCalculator.getOneRepMax(load, reps, kg),
                    "$load kg × $reps should not have fallen back to Epley",
                )
            }
        }
    }

    @Test
    fun `A light isolation set is not read as the collapsed curve would read it`() {
        // The worked counterexample: the weight-dependent curve puts a 10 kg × 12 set at 51% of
        // one-rep max, against a table value of 69%. Epley's 71% is the far better answer here.
        val oneRepMax = OneRepMaxCalculator.getOneRepMax(mass = 10.0, reps = 12, unit = kg)
        val percentage = PERCENT * 10.0 / oneRepMax
        assertTrue(percentage > 65.0, "a 10 kg × 12 set was read as $percentage% of one-rep max")
    }

    @Test
    fun `The estimate still rises with the load across the floor`() {
        (1..30).forEach { reps ->
            var previous = 0.0
            generateSequence(WEIGHT_DEPENDENT_FLOOR_KG - 5.0) { it + 0.1 }
                .takeWhile { it <= WEIGHT_DEPENDENT_FLOOR_KG + 5.0 }
                .forEach { load ->
                    val oneRepMax = OneRepMaxCalculator.getOneRepMax(load, reps, kg)
                    assertTrue(
                        oneRepMax >= previous,
                        "$reps reps: the estimate fell from $previous to $oneRepMax at $load kg",
                    )
                    previous = oneRepMax
                }
        }
    }

    @Test
    fun `A one-rep max no load reaches returns the floor`() {
        // The two curves leave a gap at the floor: a 12-rep max between Epley's 28 kg answer and
        // the weight-dependent 33.7 kg one belongs to no load at all.
        val reps = 12
        val gapTop = OneRepMaxCalculator.getOneRepMax(WEIGHT_DEPENDENT_FLOOR_KG, reps, kg)
        val gapBottom = WEIGHT_DEPENDENT_FLOOR_KG * (1.0 + reps / 30.0)
        assertTrue(gapBottom < gapTop, "expected a gap, got $gapBottom..$gapTop")
        assertEquals(
            WEIGHT_DEPENDENT_FLOOR_KG,
            OneRepMaxCalculator.loadFor((gapBottom + gapTop) / 2.0, reps, rir = 0, unit = kg),
            TOLERANCE_KG,
        )
    }

    @Test
    fun `The same physical load gives the same estimate in either unit`() {
        listOf(25.0, 60.0, 100.0, 200.0).forEach { kilograms ->
            val pounds = kg.toPounds(kilograms)
            (1..20).forEach { reps ->
                assertEquals(
                    kg.toPounds(OneRepMaxCalculator.getOneRepMax(kilograms, reps, kg)),
                    OneRepMaxCalculator.getOneRepMax(pounds, reps, lb),
                    TOLERANCE_KG,
                    "$kilograms kg and $pounds lb × $reps disagreed",
                )
                assertEquals(
                    kg.toPounds(OneRepMaxCalculator.loadFor(kilograms, reps, rir = 1, unit = kg)),
                    OneRepMaxCalculator.loadFor(pounds, reps, rir = 1, unit = lb),
                    TOLERANCE_KG,
                )
            }
        }
    }

    @Test
    fun `Reading pounds as kilograms is a different answer, which is the bug the unit fixes`() {
        val pounds = 225.0
        assertNotEquals(
            OneRepMaxCalculator.getOneRepMax(pounds, reps = 8, unit = kg),
            OneRepMaxCalculator.getOneRepMax(pounds, reps = 8, unit = lb),
        )
    }

    @Test
    fun `Epley is scale-invariant, so the light branch does not care about the unit`() {
        // Below the floor there is no ln term, so the two units agree on the multiplier even though
        // the floor itself is a kilogram quantity.
        val pounds = 20.0
        assertEquals(
            pounds * (1.0 + 10 / 30.0),
            OneRepMaxCalculator.getOneRepMax(pounds, reps = 10, unit = lb),
            TOLERANCE_KG,
        )
    }

    @Test
    fun `The curve is weight-dependent by the two to three points the reference claims`() {
        val reps = 12
        fun percentageAt(load: Double) =
            PERCENT * load / OneRepMaxCalculator.getOneRepMax(load, reps, kg)

        val atReference = percentageAt(REFERENCE_ONE_REP_MAX)
        assertEquals(-2.9, percentageAt(60.0) - atReference, 0.5)
        assertEquals(3.2, percentageAt(200.0) - atReference, 0.5)
    }

    @Test
    fun `A load is snapped to the equipment`() {
        assertEquals(62.5, OneRepMaxCalculator.roundToIncrement(62.4, 2.5), TOLERANCE_KG)
        assertEquals(62.5, OneRepMaxCalculator.roundToIncrement(63.1, 2.5), TOLERANCE_KG)
        assertEquals(65.0, OneRepMaxCalculator.roundToIncrement(63.8, 2.5), TOLERANCE_KG)
        assertEquals(61.25, OneRepMaxCalculator.roundToIncrement(61.5, 1.25), TOLERANCE_KG)
        assertEquals(60.0, OneRepMaxCalculator.roundToIncrement(62.4, 5.0), TOLERANCE_KG)
    }

    @Test
    fun `A tie rounds up`() {
        assertEquals(65.0, OneRepMaxCalculator.roundToIncrement(63.75, 2.5), TOLERANCE_KG)
        assertEquals(3.0, OneRepMaxCalculator.roundToIncrement(2.5, 1.0), TOLERANCE_KG)
    }

    @Test
    fun `An unknown equipment step leaves the load alone`() {
        assertEquals(62.4, OneRepMaxCalculator.roundToIncrement(62.4, null))
        assertEquals(62.4, OneRepMaxCalculator.roundToIncrement(62.4, 0.0))
        assertEquals(62.4, OneRepMaxCalculator.roundToIncrement(62.4, -2.5))
    }

    @Test
    fun `A set that did not happen has no estimate`() {
        assertEquals(0.0, OneRepMaxCalculator.getOneRepMax(mass = 100.0, reps = 0, unit = kg))
        assertEquals(0.0, OneRepMaxCalculator.getOneRepMax(mass = 0.0, reps = 5, unit = kg))
        assertEquals(0.0, OneRepMaxCalculator.getOneRepMax(mass = -100.0, reps = 5, unit = kg))
        assertEquals(
            0.0,
            OneRepMaxCalculator.estimatedOneRepMax(mass = 100.0, reps = 0, rir = 3, unit = kg),
        )
    }

    @Test
    fun `A target no one can lift has no load`() {
        assertEquals(0.0, OneRepMaxCalculator.loadFor(REFERENCE_ONE_REP_MAX, 0, 0, kg))
        assertEquals(
            0.0,
            OneRepMaxCalculator.loadFor(oneRepMax = 0.0, reps = 5, rir = 0, unit = kg),
        )
        assertEquals(
            0.0,
            OneRepMaxCalculator.loadFor(oneRepMax = -100.0, reps = 5, rir = 0, unit = kg),
        )
    }

    private companion object {
        const val REFERENCE_ONE_REP_MAX = 100.0
        const val PERCENT = 100.0
        const val TOLERANCE_KG = 1e-6
        val REPORTABLE_RIR = 0..4
    }
}
