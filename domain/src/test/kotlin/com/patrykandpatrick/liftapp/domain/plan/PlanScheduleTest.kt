package com.patrykandpatrick.liftapp.domain.plan

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlanScheduleTest {

    @Test
    fun `A date's position is which repetition it falls in and which day of it`() {
        val schedule = schedule(itemCount = 3, cycleCount = 4)

        assertEquals(CyclePosition(0, 0), schedule.positionOf(START))
        assertEquals(CyclePosition(0, 2), schedule.positionOf(START.plusDays(2)))
        assertEquals(CyclePosition(1, 0), schedule.positionOf(START.plusDays(3)))
        assertEquals(CyclePosition(3, 2), schedule.positionOf(START.plusDays(11)))
    }

    @Test
    fun `The day after a plan ends is outside it, not day one of a cycle it never had`() {
        val schedule = schedule(itemCount = 3, cycleCount = 4)

        assertNull(schedule.positionOf(START.plusDays(12)))
        assertFalse(START.plusDays(12) in schedule)
    }

    @Test
    fun `A date before the plan starts is outside it`() {
        val schedule = schedule(itemCount = 3, cycleCount = 4)

        assertNull(schedule.positionOf(START.minusDays(1)))
        assertFalse(START.minusDays(1) in schedule)
        assertEquals(-1, schedule.dayIndexOf(START.minusDays(1)))
    }

    @Test
    fun `The last day of the plan is still inside it`() {
        val schedule = schedule(itemCount = 3, cycleCount = 4)

        assertTrue(START.plusDays(11) in schedule)
        assertEquals(START.plusDays(11), schedule.endDate)
    }

    @Test
    fun `Cycle dates run back to back, each as long as the plan's day list`() {
        val schedule = schedule(itemCount = 3, cycleCount = 2)

        assertEquals(
            listOf(
                START to START.plusDays(2),
                START.plusDays(3) to START.plusDays(5),
            ),
            schedule.cycleDates,
        )
    }

    @Test
    fun `A plan of no days occupies none, and has no end`() {
        val schedule = schedule(itemCount = 0, cycleCount = 6)

        assertEquals(0, schedule.dayCount)
        assertNull(schedule.endDate)
        assertFalse(START in schedule)
        assertNull(schedule.positionOf(START))
    }

    @Test
    fun `Looking forward runs from the given day to the end of the plan`() {
        val schedule = schedule(itemCount = 3, cycleCount = 2)

        assertEquals(0 until 6, schedule.dayIndicesFrom(START))
        assertEquals(1 until 6, schedule.dayIndicesFrom(START, skipFirst = true))
        assertEquals(5 until 6, schedule.dayIndicesFrom(START.plusDays(5)))
    }

    @Test
    fun `Looking forward from past the plan finds nothing`() {
        val schedule = schedule(itemCount = 3, cycleCount = 2)

        assertTrue(schedule.dayIndicesFrom(START.plusDays(6)).isEmpty())
    }

    @Test
    fun `Looking forward from the last day, skipping it, finds nothing`() {
        val schedule = schedule(itemCount = 3, cycleCount = 2)

        assertTrue(schedule.dayIndicesFrom(START.plusDays(5), skipFirst = true).isEmpty())
    }

    private fun schedule(itemCount: Int, cycleCount: Int) =
        PlanSchedule(startDate = START, itemCount = itemCount, cycleCount = cycleCount)

    private companion object {
        val START: LocalDate = LocalDate.of(2026, 9, 1)
    }
}
