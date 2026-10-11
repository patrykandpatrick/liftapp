package com.patrykandpatrick.liftapp.functionality.database.exercise

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatrick.liftapp.functionality.database.workout.WorkoutEntity
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExerciseDaoRecentSetsTest {

    private val database =
        Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                Database::class.java,
            )
            .addTypeConverter(
                JsonConverters(
                    DomainModule.provideJson(
                        setOf(DatabaseModule.provideExerciseStringResourceSerializer())
                    )
                )
            )
            .allowMainThreadQueries()
            .build()

    @After
    fun close() {
        database.close()
    }

    @Test
    fun `The limit counts workouts, not sets, most recent first`() = runTest {
        seed()

        val sets = database.exerciseDao.getRecentExerciseSets(BENCH_ID, CUTOFF, limit = 2)

        assertEquals(listOf(3L, 3L, 2L, 2L), sets.map { it.exerciseSet.workoutID })
    }

    @Test
    fun `A workout starting at or after the cutoff is left out`() = runTest {
        seed()

        val sets = database.exerciseDao.getRecentExerciseSets(BENCH_ID, CUTOFF, limit = 10)

        assertEquals(setOf(1L, 2L, 3L), sets.map { it.exerciseSet.workoutID }.toSet())
    }

    @Test
    fun `A workout's sets come back in the order they were done`() = runTest {
        seed()

        val sets = database.exerciseDao.getRecentExerciseSets(BENCH_ID, CUTOFF, limit = 1)

        assertEquals(listOf(0, 1), sets.map { it.exerciseSet.setIndex })
    }

    @Test
    fun `Another exercise's sets are not counted against the limit`() = runTest {
        seed()
        val otherWorkout = insertWorkout(id = 5L, start = CUTOFF.minusHours(1))
        database.workoutDao.upsertExerciseSet(
            workoutID = otherWorkout,
            exerciseID = SQUAT_ID,
            weight = 140.0,
            weightUnit = MassUnit.Kilograms,
            reps = 5,
            setIndex = 0,
        )

        val sets = database.exerciseDao.getRecentExerciseSets(BENCH_ID, CUTOFF, limit = 1)

        assertEquals(setOf(3L), sets.map { it.exerciseSet.workoutID }.toSet())
    }

    private suspend fun seed() {
        database.routineDao.insertRoutine(RoutineEntity(id = ROUTINE_ID, name = "Push"))
        listOf(BENCH_ID to "Bench press", SQUAT_ID to "Squat").forEach { (id, name) ->
            database.exerciseDao.insert(
                ExerciseEntity(
                    id = id,
                    name = Name.Raw(name),
                    exerciseType = ExerciseType.Weight,
                    mainMuscles = emptyList(),
                    secondaryMuscles = emptyList(),
                    tertiaryMuscles = emptyList(),
                )
            )
        }
        // Workouts 1–3 a week apart before the cutoff, and 4 on it.
        (1L..4L).forEach { id ->
            val workoutID = insertWorkout(id = id, start = CUTOFF.minusWeeks(3 - id + 1))
            // Written last-first, so the order coming back is the query's and not the insert's.
            listOf(1, 0).forEach { setIndex ->
                database.workoutDao.upsertExerciseSet(
                    workoutID = workoutID,
                    exerciseID = BENCH_ID,
                    weight = 100.0,
                    weightUnit = MassUnit.Kilograms,
                    reps = 8,
                    setIndex = setIndex,
                )
            }
        }
    }

    private suspend fun insertWorkout(id: Long, start: LocalDateTime): Long =
        database.workoutDao.insertWorkout(
            WorkoutEntity(
                id = id,
                routineID = ROUTINE_ID,
                name = "Push",
                startDate = start,
                endDate = start.plusHours(1),
                notes = "",
                bodyWeight = null,
            )
        )

    private companion object {
        const val ROUTINE_ID = 7L
        const val BENCH_ID = 1L
        const val SQUAT_ID = 2L
        val CUTOFF: LocalDateTime = LocalDateTime.of(2026, 9, 29, 18, 0)
    }
}
