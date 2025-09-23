package com.patrykandpatryk.liftapp.domain.date

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

sealed class DateInterval : Serializable {

    protected abstract val periodShift: Long

    abstract val periodStartTime: LocalDateTime

    abstract val periodEndTime: LocalDateTime

    val isIncrementable: Boolean
        get() = periodEndTime.toLocalDate().plusDays(1).atStartOfDay().isBefore(LocalDateTime.now())

    fun increment(): DateInterval = shift(periodShift + 1)

    fun decrement(): DateInterval = shift(periodShift - 1)

    protected abstract fun shift(periodShift: Long): DateInterval

    data class RollingDays(val days: Int, override val periodShift: Long = 0) : DateInterval() {

        override val periodStartTime: LocalDateTime
            get() = periodEndTime.toLocalDate().minusDays(days.toLong() - 1).atStartOfDay()

        override val periodEndTime: LocalDateTime
            get() = LocalDate.now().plusDays(periodShift * days.toLong()).atEndOfDay()

        override fun shift(periodShift: Long): DateInterval = RollingDays(days, periodShift)

        companion object {
            val Standard: RollingDays
                get() = RollingDays(30)
        }
    }

    data class RollingWeek(override val periodShift: Long = 0) : DateInterval() {

        override val periodStartTime: LocalDateTime
            get() = LocalDate.now().atStartOfWeek().plusDays(7L * periodShift)

        override val periodEndTime: LocalDateTime
            get() = periodStartTime.plusDays(6L)

        override fun shift(periodShift: Long): DateInterval = RollingWeek(periodShift)
    }

    data class RollingMonth(override val periodShift: Long = 0) : DateInterval() {

        override val periodStartTime: LocalDateTime
            get() = LocalDate.now().plusMonths(periodShift).withDayOfMonth(1).atStartOfDay()

        override val periodEndTime: LocalDateTime
            get() = LocalDate.now().plusMonths(periodShift).atEndOfMonth().atEndOfDay()

        override fun shift(periodShift: Long): DateInterval = RollingMonth(periodShift)
    }

    data class RollingYear(override val periodShift: Long = 0) : DateInterval() {

        override val periodStartTime: LocalDateTime
            get() = LocalDate.now().plusYears(periodShift).withDayOfYear(1).atStartOfDay()

        override val periodEndTime: LocalDateTime
            get() = LocalDate.now().plusYears(periodShift).atEndOfYear().atEndOfDay()

        override fun shift(periodShift: Long): DateInterval = RollingYear(periodShift)
    }

    companion object {
        val exerciseOptions: List<DateInterval>
            get() = listOf(RollingDays.Standard, RollingWeek(), RollingMonth(), RollingYear())

        val bodyMeasurementOptions: List<DateInterval>
            get() =
                listOf(
                    RollingDays(7),
                    RollingDays.Standard,
                    RollingWeek(),
                    RollingMonth(),
                    RollingYear(),
                )
    }
}
