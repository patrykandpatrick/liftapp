package com.patrykandpatryk.liftapp.core.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.format.format
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.format.Formatter

@Stable
@Composable
fun DateInterval.name(): String =
    when (this) {
        is DateInterval.RollingDays ->
            stringResource(
                R.string.date_interval_rolling_days,
                days,
                pluralStringResource(R.plurals.day_count, days),
            )

        is DateInterval.RollingWeek -> stringResource(R.string.date_interval_rolling_week)

        is DateInterval.RollingMonth -> stringResource(R.string.date_interval_rolling_month)

        is DateInterval.RollingYear -> stringResource(R.string.date_interval_rolling_year)
    }

@Stable
@Composable
fun DateInterval.displayDateInterval(): String =
    when (this) {
        is DateInterval.RollingDays ->
            formatDateRange(
                startDate = periodStartTime.toLocalDate(),
                endDate = periodEndTime.toLocalDate(),
            )

        is DateInterval.RollingWeek ->
            formatDateRange(
                startDate = periodStartTime.toLocalDate(),
                endDate = periodEndTime.toLocalDate(),
            )

        is DateInterval.RollingMonth -> periodStartTime.format(Formatter.DateFormat.MonthYear)

        is DateInterval.RollingYear -> periodStartTime.format(Formatter.DateFormat.Year)
    }
