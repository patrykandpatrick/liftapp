package com.patrykandpatryk.liftapp.core.date

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.format.format
import com.patrykandpatryk.liftapp.domain.format.Formatter
import java.time.LocalDate

@Composable
fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String {
    val sameMonth = startDate.month == endDate.month
    val sameYear = startDate.year == endDate.year

    val separator = stringResource(R.string.date_range_format_separator)

    val startFormatter: Formatter.DateFormat
    val endFormatter: Formatter.DateFormat

    val dayPattern = stringResource(R.string.date_range_format_day)
    val dayMonthPattern = stringResource(R.string.date_range_format_day_month)

    if (sameMonth && sameYear) {
        startFormatter = dayFormat
        endFormatter = dayMonthFormat
    } else {
        startFormatter = dayMonthFormat
        endFormatter = dayMonthFormat
    }

    return "${startDate.format(startFormatter)}$separator${endDate.format(endFormatter)}"
}
