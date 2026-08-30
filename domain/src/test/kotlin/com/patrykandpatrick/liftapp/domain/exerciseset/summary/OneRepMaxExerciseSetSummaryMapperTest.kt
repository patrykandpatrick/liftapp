package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlinx.coroutines.runBlocking
import org.junit.Test

class OneRepMaxExerciseSetSummaryMapperTest {

    private val preferenceRepository = TestPreferenceRepository()

    private val mapper =
        OneRepMaxExerciseSetSummaryMapper(
            UnitConverter(
                formatter = Formatter(TestStringProvider, preferenceRepository),
                stringProvider = TestStringProvider,
                preferences = preferenceRepository,
            )
        )

    @Test
    fun `A set stopped short of failure reads higher than the same set taken to failure`() =
        runBlocking {
            val toFailure = oneRepMaxOf(weightSet(reps = 8, rir = 0))
            val stoppedShort = oneRepMaxOf(weightSet(reps = 8, rir = 2))
            assertTrue(
                stoppedShort > toFailure,
                "8 reps at 2 RIR ($stoppedShort) should beat 8 to failure ($toFailure)",
            )
        }

    @Test
    fun `A recorded RIR shifts the estimate along the rep axis`() = runBlocking {
        assertEquals(
            OneRepMaxCalculator.getOneRepMax(WEIGHT, reps = 10, unit = MassUnit.Kilograms),
            oneRepMaxOf(weightSet(reps = 8, rir = 2)),
        )
    }

    @Test
    fun `An unreported RIR keeps the value the chart always had`() = runBlocking {
        assertEquals(
            OneRepMaxCalculator.getOneRepMax(WEIGHT, reps = 8, unit = MassUnit.Kilograms),
            oneRepMaxOf(weightSet(reps = 8, rir = null)),
        )
    }

    @Test
    fun `A calisthenics set is read against the body weight it moved as well`() = runBlocking {
        val set =
            ExerciseSet.Calisthenics(
                weight = WEIGHT,
                bodyWeight = 80.0,
                reps = 8,
                weightUnit = MassUnit.Kilograms,
                rir = 2,
            )
        assertEquals(
            OneRepMaxCalculator.getOneRepMax(WEIGHT + 80.0, reps = 10, unit = MassUnit.Kilograms),
            oneRepMaxOf(set),
        )
    }

    @Test
    fun `An unweighted calisthenics set still has a one-rep max`() = runBlocking {
        val set =
            ExerciseSet.Calisthenics(
                weight = 0.0,
                bodyWeight = 80.0,
                reps = 8,
                weightUnit = MassUnit.Kilograms,
                rir = 0,
            )
        assertEquals(
            OneRepMaxCalculator.getOneRepMax(80.0, reps = 8, unit = MassUnit.Kilograms),
            oneRepMaxOf(set),
        )
    }

    @Test
    fun `The best set in the session is the one plotted`() = runBlocking {
        val plotted =
            oneRepMaxOf(
                weightSet(reps = 8, rir = 0),
                weightSet(reps = 8, rir = 3),
                weightSet(reps = 5, rir = 0),
            )
        assertEquals(
            OneRepMaxCalculator.getOneRepMax(WEIGHT, reps = 11, unit = MassUnit.Kilograms),
            plotted,
        )
    }

    @Test
    fun `A set with no load is not plotted as a one-rep max`() = runBlocking {
        assertEquals(0.0, oneRepMaxOf(ExerciseSet.Reps(reps = 20, rir = 1)))
        assertEquals(0.0, oneRepMaxOf(ExerciseSet.Time(duration = Duration.ZERO)))
    }

    @Test
    fun `One point is plotted per session, at its start date`() = runBlocking {
        val first = LocalDateTime.of(2026, 8, 1, 9, 0)
        val second = LocalDateTime.of(2026, 8, 8, 9, 0)
        val (x, _) =
            mapper(
                    listOf(
                        group(first, weightSet(reps = 8, rir = 1)),
                        group(second, weightSet(reps = 8, rir = 1)),
                    )
                )
                .single()
        assertEquals(
            listOf(
                first.toLocalDate().toEpochDay().toDouble(),
                second.toLocalDate().toEpochDay().toDouble(),
            ),
            x,
        )
    }

    private suspend fun oneRepMaxOf(vararg sets: ExerciseSet): Double =
        mapper(listOf(group(LocalDateTime.of(2026, 8, 1, 9, 0), *sets))).single().second.single()

    private fun group(startDate: LocalDateTime, vararg sets: ExerciseSet) =
        ExerciseSetGroup(
            workoutID = 1L,
            workoutName = "Push",
            exerciseID = 1L,
            sets = sets.toList(),
            workoutStartDate = startDate,
        )

    private fun weightSet(reps: Int, rir: Int?) =
        ExerciseSet.Weight(
            weight = WEIGHT,
            reps = reps,
            weightUnit = MassUnit.Kilograms,
            rir = rir,
        )

    private companion object {
        const val WEIGHT = 100.0
    }
}
