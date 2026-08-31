package com.patrykandpatrick.liftapp.functionality.database.target

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanEntity
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanItemEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.ExerciseWithRoutineItemEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineItemEntity
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Guards the join in `getTargetsWithExercises`, which reaches through the routine for the order the
 * exercises are laid out in.
 *
 * A routine has many items, and joining them all in to find one exercise's position returned the
 * same target once per item — which the editor's `LazyColumn` met as a repeated key and crashed on.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class TargetDaoTest {

    private val json =
        DomainModule.provideJson(setOf(DatabaseModule.provideExerciseStringResourceSerializer()))

    private val database =
        Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                Database::class.java,
            )
            .addTypeConverter(JsonConverters(json))
            .allowMainThreadQueries()
            .build()

    @After fun close() = database.close()

    @Test
    fun `Every exercise appears once, however many items the routine has`() = runTest {
        seedRoutineOfFourItems()

        val rows = database.targetDao.getTargetsWithExercises(PLAN_ID, 0, 0).first()

        assertEquals(4, rows.size)
        assertEquals(
            listOf(FIRST_ID, FIRST_ID + 1, FIRST_ID + 2, FIRST_ID + 3),
            rows.map { it.target.exerciseID },
        )
    }

    @Test
    fun `The routine's order is the order they come back in`() = runTest {
        seedRoutineOfFourItems()

        val rows = database.targetDao.getTargetsWithExercises(PLAN_ID, 0, 0).first()

        assertEquals(
            listOf("Exercise 0", "Exercise 1", "Exercise 2", "Exercise 3"),
            rows.map { (it.exerciseName as Name.Raw).value },
        )
    }

    @Test
    fun `Each row carries the name and the routine it belongs to`() = runTest {
        seedRoutineOfFourItems()

        val row = database.targetDao.getTargetsWithExercises(PLAN_ID, 0, 0).first().first()

        assertEquals("Push", row.routineName)
        assertEquals(ExerciseType.Weight, row.exerciseType)
    }

    @Test
    fun `Another cycle's targets are not returned`() = runTest {
        seedRoutineOfFourItems()
        database.targetDao.upsertTargets(listOf(target(exerciseID = FIRST_ID, cycleIndex = 1)))

        val rows = database.targetDao.getTargetsWithExercises(PLAN_ID, 1, 0).first()

        assertEquals(1, rows.size)
        assertEquals(1, rows.single().target.cycleIndex)
    }

    /** Four items, one exercise each — the shape that multiplied every target by four. */
    private suspend fun seedRoutineOfFourItems() {
        database.routineDao.insertRoutine(RoutineEntity(id = ROUTINE_ID, name = "Push"))
        database.planDao.upsertPlan(
            PlanEntity(id = PLAN_ID, name = "Upper/Lower", description = "", itemCount = 1)
        )
        database.planDao.upsertPlanItems(
            listOf(PlanItemEntity(planId = PLAN_ID, routineId = ROUTINE_ID, orderIndex = 0))
        )

        repeat(ITEM_COUNT) { index ->
            val exerciseID = FIRST_ID + index
            database.exerciseDao.insert(
                ExerciseEntity(
                    id = exerciseID,
                    name = Name.Raw("Exercise $index"),
                    exerciseType = ExerciseType.Weight,
                    mainMuscles = emptyList(),
                    secondaryMuscles = emptyList(),
                    tertiaryMuscles = emptyList(),
                )
            )
            val itemID =
                database.routineDao.insert(
                    RoutineItemEntity(
                        routineID = ROUTINE_ID,
                        orderIndex = index,
                        type = RoutineItemType.Exercise,
                    )
                )
            database.routineDao.insert(
                listOf(
                    ExerciseWithRoutineItemEntity(
                        routineItemID = itemID,
                        exerciseID = exerciseID,
                        orderIndex = 0,
                    )
                )
            )
            database.targetDao.upsertTargets(listOf(target(exerciseID, cycleIndex = 0)))
        }
    }

    private fun target(exerciseID: Long, cycleIndex: Int) =
        ExerciseTargetEntity(
            planID = PLAN_ID,
            cycleIndex = cycleIndex,
            itemOrderIndex = 0,
            exerciseID = exerciseID,
            sets = List(3) { SetTarget(minReps = 8, maxReps = 12, rir = 2) },
            restTimeMillis = 120_000L,
            distanceUnit = com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit.Kilometer,
        )

    private companion object {
        const val PLAN_ID = 1L
        const val ROUTINE_ID = 7L
        const val FIRST_ID = 8L
        const val ITEM_COUNT = 4
    }
}
