package com.patrykandpatrick.liftapp.domain.muscle

import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutsInRangeContract
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetMuscleVolumeUseCaseTest {

    @Test
    fun `Each completed set is credited to the muscles its exercise names`() = runTest {
        val volume =
            volumeOf(
                workout(
                    TODAY,
                    exercise(
                        mainMuscles = listOf(Muscle.Chest),
                        secondaryMuscles = listOf(Muscle.Triceps, Muscle.FrontDelts),
                        tertiaryMuscles = listOf(Muscle.Abs),
                        sets = List(4) { weightSet() },
                    ),
                )
            )

        assertEquals(4.0, volume[Muscle.Chest])
        assertEquals(2.0, volume[Muscle.Triceps])
        assertEquals(2.0, volume[Muscle.FrontDelts])
        assertEquals(1.0, volume[Muscle.Abs])
        assertEquals(0.0, volume[Muscle.Lats])
    }

    @Test
    fun `Volume on one muscle accumulates across the window`() = runTest {
        val press =
            exercise(
                mainMuscles = listOf(Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Triceps),
                sets = List(3) { weightSet() },
            )
        val bench =
            exercise(
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts, Muscle.Triceps),
                sets = List(5) { weightSet() },
            )

        val volume = volumeOf(workout(TODAY.minusDays(6), press), workout(TODAY, bench))

        assertEquals(5.5, volume[Muscle.FrontDelts])
        assertEquals(4.0, volume[Muscle.Triceps])
        assertEquals(5.0, volume[Muscle.Chest])
    }

    @Test
    fun `A workout outside the window is left out`() = runTest {
        val volume =
            volumeOf(
                workout(
                    TODAY.minusDays(7),
                    exercise(mainMuscles = listOf(Muscle.Lats), sets = List(3) { weightSet() }),
                )
            )

        assertEquals(MuscleVolume.Empty, volume)
    }

    @Test
    fun `Sets that were never completed are not credited`() = runTest {
        val volume =
            volumeOf(
                workout(
                    TODAY,
                    exercise(
                        mainMuscles = listOf(Muscle.Lats),
                        sets =
                            listOf(
                                weightSet(),
                                weightSet(weight = 0.0, reps = 0),
                                weightSet(weight = 0.0, reps = 0),
                            ),
                    ),
                )
            )

        assertEquals(1.0, volume[Muscle.Lats])
    }

    @Test
    fun `A cardio session is not a working set of anything`() = runTest {
        val volume =
            volumeOf(
                workout(
                    TODAY,
                    exercise(
                        exerciseType = ExerciseType.Cardio,
                        mainMuscles = listOf(Muscle.Quadriceps),
                        secondaryMuscles = listOf(Muscle.Calves),
                        sets =
                            listOf(
                                ExerciseSet.Cardio(
                                    duration = 30.minutes,
                                    distance = 5.0,
                                    kcal = 320.0,
                                    distanceUnit = LongDistanceUnit.Kilometer,
                                )
                            ),
                    ),
                )
            )

        assertEquals(0.0, volume[Muscle.Quadriceps])
        assertEquals(0.0, volume[Muscle.Calves])
    }

    @Test
    fun `A held position is a working set`() = runTest {
        val volume =
            volumeOf(
                workout(
                    TODAY,
                    exercise(
                        exerciseType = ExerciseType.Time,
                        mainMuscles = listOf(Muscle.Abs),
                        sets = List(3) { ExerciseSet.Time(duration = 1.minutes) },
                    ),
                )
            )

        assertEquals(3.0, volume[Muscle.Abs])
    }

    @Test
    fun `A window longer than a week comes back as a weekly average`() = runTest {
        val bench =
            exercise(
                mainMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps),
                sets = List(6) { weightSet() },
            )

        val week = volumeOf(DateInterval.RollingDays(days = 7), workout(TODAY, bench))
        val fourWeeks = volumeOf(DateInterval.RollingDays(days = 28), workout(TODAY, bench))

        assertEquals(6.0, week[Muscle.Chest])
        assertEquals(1.5, fourWeeks[Muscle.Chest])
    }

    @Test
    fun `A workout still in progress credits nothing`() = runTest {
        val volume =
            volumeOf(
                workout(
                    TODAY,
                    exercise(mainMuscles = listOf(Muscle.Lats), sets = List(4) { weightSet() }),
                    finished = false,
                )
            )

        assertEquals(MuscleVolume.Empty, volume)
    }

    @Test
    fun `A window with nothing logged has no volume on any muscle`() = runTest {
        assertEquals(MuscleVolume.Empty, volumeOf())
    }

    private suspend fun volumeOf(vararg workouts: Workout): MuscleVolume =
        volumeOf(ROLLING_WEEK, *workouts)

    private suspend fun volumeOf(
        dateInterval: DateInterval,
        vararg workouts: Workout,
    ): MuscleVolume =
        GetMuscleVolumeUseCase(
                GetPastWorkoutsInRangeContract { start, endExclusive ->
                    flowOf(
                        workouts.filter { workout ->
                            // Mirrors `workout_end_date IS NOT NULL AND workout_start_date >= ?
                            // AND workout_start_date < ?` — the real query drops a workout that
                            // has not ended, whatever is logged inside it.
                            workout.endDate != null &&
                                !workout.startDate.isBefore(start) &&
                                workout.startDate.isBefore(endExclusive)
                        }
                    )
                }
            )(dateInterval)
            .first()

    private companion object {
        val TODAY: LocalDate = LocalDate.now()

        val ROLLING_WEEK = DateInterval.RollingDays(days = 7)

        fun weightSet(weight: Double = 100.0, reps: Int = 8) =
            ExerciseSet.Weight(weight = weight, reps = reps, weightUnit = MassUnit.Kilograms)

        fun exercise(
            exerciseType: ExerciseType = ExerciseType.Weight,
            mainMuscles: List<Muscle> = emptyList(),
            secondaryMuscles: List<Muscle> = emptyList(),
            tertiaryMuscles: List<Muscle> = emptyList(),
            sets: List<ExerciseSet> = emptyList(),
        ) =
            Workout.Exercise(
                id = 0,
                name = Name.Raw("Exercise"),
                exerciseType = exerciseType,
                mainMuscles = mainMuscles,
                secondaryMuscles = secondaryMuscles,
                tertiaryMuscles = tertiaryMuscles,
                goal = Workout.Goal.default,
                sets = sets,
            )

        fun workout(
            date: LocalDate,
            vararg exercises: Workout.Exercise,
            finished: Boolean = true,
        ): Workout {
            val start = LocalDateTime.of(date, java.time.LocalTime.of(10, 0))
            return Workout(
                id = 0,
                routineID = 0,
                name = "Workout",
                startDate = start,
                endDate = start.plusHours(1).takeIf { finished },
                notes = "",
                exercises = exercises.toList(),
            )
        }
    }
}
