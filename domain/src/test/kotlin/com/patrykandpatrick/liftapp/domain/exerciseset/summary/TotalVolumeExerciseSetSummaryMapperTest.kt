package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
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

class TotalVolumeExerciseSetSummaryMapperTest {

    private val preferenceRepository = TestPreferenceRepository()

    private val mapper =
        TotalVolumeExerciseSetSummaryMapper(
            UnitConverter(
                formatter = Formatter(TestStringProvider, preferenceRepository),
                stringProvider = TestStringProvider,
                preferences = preferenceRepository,
            )
        )

    @Test
    fun `A weight set is its load times its reps`() = runBlocking {
        assertEquals(
            listOf(500.0),
            volumeOf(ExerciseSet.Weight(weight = 100.0, reps = 5, weightUnit = MassUnit.Kilograms)),
        )
    }

    @Test
    fun `A calisthenics set counts the body weight it moved`() = runBlocking {
        assertEquals(
            listOf(850.0),
            volumeOf(
                ExerciseSet.Calisthenics(
                    weight = 5.0,
                    bodyWeight = 80.0,
                    reps = 10,
                    weightUnit = MassUnit.Kilograms,
                )
            ),
        )
    }

    @Test
    fun `An unweighted calisthenics set is not weightless`() = runBlocking {
        assertEquals(
            listOf(640.0),
            volumeOf(
                ExerciseSet.Calisthenics(
                    weight = 0.0,
                    bodyWeight = 80.0,
                    reps = 8,
                    weightUnit = MassUnit.Kilograms,
                )
            ),
        )
    }

    @Test
    fun `A set carrying no load plots no point`() = runBlocking {
        assertTrue(volumeOf(ExerciseSet.Reps(reps = 20)).isEmpty())
        assertTrue(volumeOf(ExerciseSet.Time(duration = Duration.ZERO)).isEmpty())
    }

    /** One series per set index, so a session of three sets plots three lines. */
    private suspend fun volumeOf(vararg sets: ExerciseSet): List<Double> =
        mapper(listOf(group(*sets))).flatMap { (_, y) -> y }

    private fun group(vararg sets: ExerciseSet) =
        ExerciseSetGroup(
            workoutID = 1L,
            workoutName = "Pull",
            exerciseID = 1L,
            sets = sets.toList(),
            workoutStartDate = LocalDateTime.of(2026, 8, 1, 9, 0),
        )
}
