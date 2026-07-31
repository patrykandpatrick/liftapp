package com.patrykandpatrick.liftapp.domain.exerciseset

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetExerciseStatisticsUseCaseTest {

    private val preferences = TestPreferenceRepository()
    private val formatter = Formatter(TestStringProvider, preferences)
    private val unitConverter = UnitConverter(formatter, TestStringProvider, preferences)
    private val getStatistics = GetExerciseStatisticsUseCase(unitConverter)

    @Test
    fun `Weight statistics include totals and extrema in the preferred unit`() = runTest {
        val pounds = 100.0
        val statistics =
            assertIs<ExerciseStatistics.Weight>(
                getStatistics(
                    ExerciseType.Weight,
                    groups(
                        ExerciseSet.Weight(50.0, 10, MassUnit.Kilograms),
                        ExerciseSet.Weight(pounds, 5, MassUnit.Pounds),
                        ExerciseSet.Weight.empty(MassUnit.Kilograms),
                    ),
                )
            )
        val poundsInKilograms = MassUnit.Pounds.toKilograms(pounds)

        assertEquals(50.0 * 10 + poundsInKilograms * 5, statistics.totalVolume)
        assertEquals(15, statistics.totalReps)
        assertEquals(poundsInKilograms, statistics.minimumWeight)
        assertEquals(50.0, statistics.maximumWeight)
        assertEquals(MassUnit.Kilograms, statistics.massUnit)
    }

    @Test
    fun `Calisthenics statistics use body weight plus additional weight`() = runTest {
        val statistics =
            assertIs<ExerciseStatistics.Weight>(
                getStatistics(
                    ExerciseType.Calisthenics,
                    groups(
                        ExerciseSet.Calisthenics(
                            weight = 10.0,
                            bodyWeight = 80.0,
                            reps = 5,
                            weightUnit = MassUnit.Kilograms,
                        ),
                        ExerciseSet.Calisthenics(
                            weight = 0.0,
                            bodyWeight = 80.0,
                            reps = 10,
                            weightUnit = MassUnit.Kilograms,
                        ),
                    ),
                )
            )

        assertEquals(1_250.0, statistics.totalVolume)
        assertEquals(15, statistics.totalReps)
        assertEquals(80.0, statistics.minimumWeight)
        assertEquals(90.0, statistics.maximumWeight)
    }

    @Test
    fun `Reps statistics include total minimum and maximum`() = runTest {
        val statistics =
            assertIs<ExerciseStatistics.Reps>(
                getStatistics(
                    ExerciseType.Reps,
                    groups(ExerciseSet.Reps(8), ExerciseSet.Reps(12), ExerciseSet.Reps(10)),
                )
            )

        assertEquals(30, statistics.totalReps)
        assertEquals(8, statistics.minimumReps)
        assertEquals(12, statistics.maximumReps)
    }

    @Test
    fun `Time statistics include total minimum and maximum duration`() = runTest {
        val statistics =
            assertIs<ExerciseStatistics.Time>(
                getStatistics(
                    ExerciseType.Time,
                    groups(
                        ExerciseSet.Time(2.minutes),
                        ExerciseSet.Time(5.minutes),
                        ExerciseSet.Time(3.minutes),
                    ),
                )
            )

        assertEquals(10.minutes, statistics.totalDuration)
        assertEquals(2.minutes, statistics.minimumDuration)
        assertEquals(5.minutes, statistics.maximumDuration)
    }

    @Test
    fun `Cardio statistics normalize distance and aggregate duration`() = runTest {
        val statistics =
            assertIs<ExerciseStatistics.Cardio>(
                getStatistics(
                    ExerciseType.Cardio,
                    groups(
                        ExerciseSet.Cardio(
                            duration = 20.minutes,
                            distance = 5.0,
                            kcal = 0.0,
                            distanceUnit = LongDistanceUnit.Kilometer,
                        ),
                        ExerciseSet.Cardio(
                            duration = 30.minutes,
                            distance = 3.0,
                            kcal = 0.0,
                            distanceUnit = LongDistanceUnit.Mile,
                        ),
                    ),
                )
            )

        assertEquals(50.minutes, statistics.totalDuration)
        assertEquals(20.minutes, statistics.minimumDuration)
        assertEquals(30.minutes, statistics.maximumDuration)
        assertEquals(
            5.0 + LongDistanceUnit.Mile.toKilometers(3.0),
            statistics.totalDistance,
        )
        assertEquals(LongDistanceUnit.Kilometer, statistics.distanceUnit)
    }

    @Test
    fun `No completed sets produce no statistics`() = runTest {
        assertNull(
            getStatistics(
                ExerciseType.Weight,
                groups(ExerciseSet.Weight.empty(MassUnit.Kilograms)),
            )
        )
    }

    private fun groups(vararg sets: ExerciseSet): List<ExerciseSetGroup> =
        listOf(
            ExerciseSetGroup(
                workoutID = 1,
                workoutName = "Workout",
                exerciseID = 1,
                sets = sets.toList(),
                workoutStartDate = LocalDateTime.of(2026, 7, 29, 10, 0),
            )
        )
}
