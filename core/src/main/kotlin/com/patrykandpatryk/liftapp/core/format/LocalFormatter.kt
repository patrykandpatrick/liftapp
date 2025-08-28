package com.patrykandpatryk.liftapp.core.format

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.patrykandpatryk.liftapp.domain.format.Formatter
import java.time.LocalDate
import java.time.LocalDateTime

val LocalFormatter: ProvidableCompositionLocal<Formatter> = staticCompositionLocalOf {
    error("No Formatter provided")
}

@Composable
fun LocalDateTime.format(format: Formatter.DateFormat): String =
    LocalFormatter.current.formatDate(this, format)

@Composable
fun LocalDate.format(format: Formatter.DateFormat): String =
    LocalFormatter.current.formatDate(this, format)
