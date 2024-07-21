package com.patrykandpatryk.liftapp.domain.format

import java.time.LocalDateTime

data class FormattedDate(
    val dateShort: String,
    val dateLong: String,
    val timeShort: String,
    val timeLong: String,
    val localDateTime: LocalDateTime,
) : Comparable<FormattedDate>, java.io.Serializable {
    val year: Int = localDateTime.year

    val month: Int = localDateTime.month.value

    val day: Int = localDateTime.dayOfYear

    val hour: Int = localDateTime.hour

    val minute: Int = localDateTime.minute

    val second: Int = localDateTime.second

    val millis: Long = localDateTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()

    override fun compareTo(other: FormattedDate): Int = localDateTime.compareTo(other.localDateTime)

    companion object {
        val Empty = FormattedDate("", "", "", "", LocalDateTime.of(0, 1, 1, 0, 0))
    }
}
