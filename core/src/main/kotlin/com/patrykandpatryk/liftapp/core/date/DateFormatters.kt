package com.patrykandpatryk.liftapp.core.date

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String {
    val sameMonth = startDate.month == endDate.month
    val sameYear = startDate.year == endDate.year

    val separator = stringResource(R.string.date_range_format_separator)

    val startFormatter: DateTimeFormatter
    val endFormatter: DateTimeFormatter

    val dayPattern = stringResource(R.string.date_range_format_day)
    val dayMonthPattern = stringResource(R.string.date_range_format_day_month)

    if (sameMonth && sameYear) {
        startFormatter = DateTimeFormatter.ofPattern(dayPattern)
        endFormatter = DateTimeFormatter.ofPattern(dayMonthPattern)
    } else {
        startFormatter = DateTimeFormatter.ofPattern(dayMonthPattern)
        endFormatter = DateTimeFormatter.ofPattern(dayMonthPattern)
    }

    return "${startDate.format(startFormatter)}$separator${endDate.format(endFormatter)}"
}
