package com.patrykandpatrick.liftapp.functionality.database.plan

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Guards the schedule table's one invariant: it holds the days of the plan being run, and nothing
 * else. Activation used to append, so a plan started twice had two sets of days on the same dates.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PlanScheduleDaoTest {

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

    @After fun close() = database.close()

    @Test
    fun `Activating a plan again replaces its days rather than adding a second set`() = runTest {
        seedPlans()

        database.planDao.replacePlanItemSchedule(scheduleFor(PLAN_ID, DATE))
        database.planDao.replacePlanItemSchedule(scheduleFor(PLAN_ID, DATE))

        assertEquals(1, database.planDao.getScheduledRoutine(PLAN_ID, DATE).first().size)
    }

    @Test
    fun `Switching plans leaves none of the old plan's days behind`() = runTest {
        seedPlans()

        database.planDao.replacePlanItemSchedule(scheduleFor(PLAN_ID, DATE))
        database.planDao.replacePlanItemSchedule(scheduleFor(OTHER_PLAN_ID, DATE))

        assertTrue(database.planDao.getScheduledRoutine(PLAN_ID, DATE).first().isEmpty())
        assertEquals(1, database.planDao.getScheduledRoutine(OTHER_PLAN_ID, DATE).first().size)
    }

    @Test
    fun `A day is read from the plan asked for, not whichever row the date matched`() = runTest {
        seedPlans()
        // Written past replacePlanItemSchedule, the way a restored archive arrives: two plans with
        // a row on the same date, which the table's own invariant would otherwise rule out.
        database.planDao.insertPlanItemSchedule(
            scheduleFor(PLAN_ID, DATE) + scheduleFor(OTHER_PLAN_ID, DATE)
        )

        assertEquals(1, database.planDao.getScheduledRoutine(PLAN_ID, DATE).first().size)
        assertEquals(1, database.planDao.getScheduledRoutine(OTHER_PLAN_ID, DATE).first().size)
    }

    @Test
    fun `A date the plan does not cover has nothing scheduled`() = runTest {
        seedPlans()

        database.planDao.replacePlanItemSchedule(scheduleFor(PLAN_ID, DATE))

        assertTrue(
            database.planDao.getScheduledRoutine(PLAN_ID, DATE.plusDays(1)).first().isEmpty()
        )
    }

    private suspend fun seedPlans() {
        database.routineDao.insertRoutine(RoutineEntity(id = ROUTINE_ID, name = "Push"))
        listOf(PLAN_ID, OTHER_PLAN_ID).forEach { id ->
            database.planDao.upsertPlan(
                PlanEntity(id = id, name = "Plan $id", description = "", itemCount = 1)
            )
        }
    }

    private fun scheduleFor(planID: Long, date: LocalDate) =
        listOf(
            PlanItemSchedule(
                planID = planID,
                routineID = ROUTINE_ID,
                date = date,
                cycleIndex = 0,
                itemOrderIndex = 0,
            )
        )

    private companion object {
        const val PLAN_ID = 1L
        const val OTHER_PLAN_ID = 2L
        const val ROUTINE_ID = 7L
        val DATE: LocalDate = LocalDate.of(2026, 9, 1)
    }
}
