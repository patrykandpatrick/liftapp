package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private class EpochDayCartesianValueFormatter(
    private val regularDateFormatter: DateTimeFormatter,
    private val dayOfWeekFormatter: DateTimeFormatter,
    private val dayOfMonthFormatter: DateTimeFormatter,
    private val monthFormatter: DateTimeFormatter,
) : CartesianValueFormatter {
    override fun format(
        context: CartesianMeasuringContext,
        value: Double,
        verticalAxisPosition: Axis.Position.Vertical?,
    ): CharSequence {
        val localDate = LocalDate.ofEpochDay(value.toLong())
        val dateInterval = context.model.extraStore.getOrNull(ExtraStoreKey.DateInterval)
        val isRangeWithinTwoMonths =
            dateInterval?.run { ChronoUnit.MONTHS.between(periodStartTime, periodEndTime) <= 2 }
                ?: false
        return when {
            dateInterval is DateInterval.RollingWeek -> dayOfWeekFormatter
            isRangeWithinTwoMonths || dateInterval is DateInterval.RollingMonth ->
                dayOfMonthFormatter
            dateInterval is DateInterval.RollingYear -> monthFormatter
            else -> regularDateFormatter
        }.format(localDate)
    }
}

@Composable
fun rememberEpochDayCartesianValueFormatter(): CartesianValueFormatter {
    val datePattern = stringResource(R.string.date_day_month_s)
    return remember(datePattern) {
        EpochDayCartesianValueFormatter(
            regularDateFormatter = DateTimeFormatter.ofPattern(datePattern),
            dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE"),
            dayOfMonthFormatter = DateTimeFormatter.ofPattern("d"),
            monthFormatter = DateTimeFormatter.ofPattern("M"),
        )
    }
}
