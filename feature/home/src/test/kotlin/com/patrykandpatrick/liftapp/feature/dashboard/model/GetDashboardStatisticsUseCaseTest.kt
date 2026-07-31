package com.patrykandpatrick.liftapp.feature.dashboard.model

import com.patrykandpatrick.liftapp.domain.date.GetFirstDayOfWeekUseCase
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.unit.GetPreferredMassUnitUseCase
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutsInRangeContract
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetDashboardStatisticsUseCaseTest {

    private val preferenceRepository = TestPreferenceRepository()

    private val formatter = Formatter(TestStringProvider, MutableStateFlow(true))

    private val unitConverter = UnitConverter(formatter, TestStringProvider, preferenceRepository)

    private suspend fun statisticsOf(
        vararg workouts: Workout,
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    ): DashboardStatistics =
        GetDashboardStatisticsUseCase(
                getPastWorkouts =
                    GetPastWorkoutsInRangeContract { start, endExclusive ->
                        flowOf(
                            workouts.filter { workout ->
                                !workout.startDate.isBefore(start) &&
                                    workout.startDate.isBefore(endExclusive)
                            }
                        )
                    },
                getPreferredMassUnit = GetPreferredMassUnitUseCase { flowOf(MassUnit.Kilograms) },
                getFirstDayOfWeek = GetFirstDayOfWeekUseCase { flowOf(firstDayOfWeek) },
                unitConverter = unitConverter,
            )(today = WEDNESDAY)
            .first()

    @Test
    fun `Workouts from other weeks are left out`() = runTest {
        val statistics =
            statisticsOf(
                workout(MONDAY, sets = listOf(weightSet(100.0, reps = 5))),
                workout(MONDAY.minusDays(1), sets = listOf(weightSet(100.0, reps = 5))),
                workout(SUNDAY.plusDays(1), sets = listOf(weightSet(100.0, reps = 5))),
            )

        assertEquals(1, statistics.workouts)
        assertEquals(500.0, statistics.volume)
        assertEquals(5, statistics.reps)
    }

    @Test
    fun `Both ends of the week are included`() = runTest {
        val statistics = statisticsOf(workout(MONDAY), workout(SUNDAY))

        assertEquals(2, statistics.workouts)
    }

    @Test
    fun `The week the preference starts on is the week that counts`() = runTest {
        val statistics =
            statisticsOf(
                workout(MONDAY.minusDays(1)),
                workout(MONDAY),
                workout(SUNDAY),
                firstDayOfWeek = DayOfWeek.SUNDAY,
            )

        // Starting on Sunday moves the week to the 19th–25th, which takes in the 19th and drops the
        // 26th.
        assertEquals(2, statistics.workouts)
    }

    @Test
    fun `Sets that were never completed do not count`() = runTest {
        val statistics =
            statisticsOf(
                workout(MONDAY, sets = listOf(weightSet(100.0, reps = 5), weightSet(0.0, reps = 0)))
            )

        assertEquals(500.0, statistics.volume)
        assertEquals(5, statistics.reps)
    }

    @Test
    fun `Weight recorded in pounds is counted in the preferred unit`() = runTest {
        val statistics =
            statisticsOf(
                workout(MONDAY, sets = listOf(weightSet(100.0, reps = 1, unit = MassUnit.Pounds)))
            )

        assertEquals(MassUnit.Pounds.toKilograms(100.0), statistics.volume)
    }

    @Test
    fun `Time exercised is the span between a workout's start and end`() = runTest {
        val statistics = statisticsOf(workout(MONDAY, hours = 2), workout(SUNDAY, hours = 1))

        assertEquals(3.hours, statistics.timeExercised)
    }

    @Test
    fun `A workout still in progress contributes no time`() = runTest {
        val statistics = statisticsOf(workout(MONDAY, hours = null))

        assertEquals(1, statistics.workouts)
        assertEquals(kotlin.time.Duration.ZERO, statistics.timeExercised)
    }

    private companion object {
        val MONDAY: LocalDate = LocalDate.of(2026, 7, 20)
        val WEDNESDAY: LocalDate = LocalDate.of(2026, 7, 22)
        val SUNDAY: LocalDate = LocalDate.of(2026, 7, 26)

        fun weightSet(weight: Double, reps: Int, unit: MassUnit = MassUnit.Kilograms) =
            ExerciseSet.Weight(weight = weight, reps = reps, weightUnit = unit)

        fun workout(
            date: LocalDate,
            hours: Long? = 1,
            sets: List<ExerciseSet> = emptyList(),
        ): Workout {
            val start = date.atTime(10, 0)
            return Workout(
                id = 0,
                routineID = 0,
                name = "Workout",
                startDate = start,
                endDate = hours?.let { start.plusHours(it) },
                notes = "",
                exercises =
                    listOf(
                        Workout.Exercise(
                            id = 0,
                            name = Name.Raw("Exercise"),
                            exerciseType = ExerciseType.Weight,
                            mainMuscles = emptyList(),
                            secondaryMuscles = emptyList(),
                            tertiaryMuscles = emptyList(),
                            goal = Workout.Goal.default,
                            sets = sets,
                        )
                    ),
            )
        }

        private fun LocalDate.atTime(hour: Int, minute: Int): LocalDateTime =
            LocalDateTime.of(this, java.time.LocalTime.of(hour, minute))
    }
}
