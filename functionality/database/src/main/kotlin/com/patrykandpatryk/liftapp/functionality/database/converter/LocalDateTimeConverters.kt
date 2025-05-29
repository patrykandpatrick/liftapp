package com.patrykandpatryk.liftapp.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ProvidedTypeConverter
object LocalDateTimeConverters {

    @TypeConverter
    fun toString(localDateTime: LocalDateTime): String =
        localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @TypeConverter fun toLocalDateTime(string: String) = LocalDateTime.parse(string)

    @TypeConverter
    fun toString(localDate: LocalDate): String = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter fun toLocalDate(string: String): LocalDate = LocalDate.parse(string)
}
