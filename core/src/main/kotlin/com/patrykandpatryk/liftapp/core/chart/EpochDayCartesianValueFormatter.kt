package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatryk.liftapp.core.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private class EpochDayCartesianValueFormatter(private val dateTimeFormatter: DateTimeFormatter) :
    CartesianValueFormatter {
    override fun format(
        context: CartesianMeasuringContext,
        value: Double,
        verticalAxisPosition: Axis.Position.Vertical?,
    ): CharSequence {
        val localDate = LocalDate.ofEpochDay(value.toLong())
        return dateTimeFormatter.format(localDate)
    }
}

@Composable
fun rememberEpochDayCartesianValueFormatter(): CartesianValueFormatter {
    val datePattern = stringResource(R.string.date_day_month_s)
    return remember(datePattern) {
        EpochDayCartesianValueFormatter(DateTimeFormatter.ofPattern(datePattern))
    }
}
