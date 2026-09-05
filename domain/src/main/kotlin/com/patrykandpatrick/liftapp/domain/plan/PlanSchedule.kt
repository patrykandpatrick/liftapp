package com.patrykandpatrick.liftapp.domain.plan

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * A plan laid out on the calendar: [itemCount] days repeated [cycleCount] times from [startDate].
 *
 * Every question about where a date falls in a plan is division and remainder over the plan's
 * length, and it was written out in three places before this — which is three chances to disagree
 * about whether a plan that has run out still has a current day. Everything that needs a cycle or a
 * day index asks here instead.
 *
 * A date outside the plan has no position at all, rather than an extrapolated one: the day after a
 * six-cycle plan ends is not day one of cycle seven.
 */
class PlanSchedule(val startDate: LocalDate, val itemCount: Int, val cycleCount: Int) {

    /**
     * Total days the plan occupies. Zero for a plan with no days, which has no positions either.
     */
    val dayCount: Int = itemCount * cycleCount

    /** Null when the plan occupies no days, so callers cannot form an empty range by accident. */
    val endDate: LocalDate? = startDate.plusDays(dayCount - 1L).takeIf { dayCount > 0 }

    /** The first and last date of each cycle, in order. */
    val cycleDates: List<Pair<LocalDate, LocalDate>> =
        List(cycleCount) { cycleIndex ->
            val first = startDate.plusDays(cycleIndex.toLong() * itemCount)
            first to first.plusDays(itemCount - 1L)
        }

    /** Days from the start, negative before it. Outside the plan's span this is not a position. */
    fun dayIndexOf(date: LocalDate): Int = ChronoUnit.DAYS.between(startDate, date).toInt()

    operator fun contains(date: LocalDate): Boolean = dayIndexOf(date) in 0 until dayCount

    fun positionOf(date: LocalDate): CyclePosition? =
        dayIndexOf(date).takeIf { it in 0 until dayCount }?.let(::positionOfDay)

    fun positionOfDay(dayIndex: Int): CyclePosition =
        CyclePosition(cycleIndex = dayIndex / itemCount, itemOrderIndex = dayIndex % itemCount)

    /**
     * Day indices from [date] to the end of the plan, for callers looking forward — the next
     * training day, the next time a routine comes round. Empty when [date] is past the plan.
     */
    fun dayIndicesFrom(date: LocalDate, skipFirst: Boolean = false): IntRange {
        if (date !in this) return IntRange.EMPTY
        return (dayIndexOf(date) + if (skipFirst) 1 else 0) until dayCount
    }
}

/** Where one date sits in a plan: which repetition of it, and which day of that repetition. */
data class CyclePosition(val cycleIndex: Int, val itemOrderIndex: Int)
