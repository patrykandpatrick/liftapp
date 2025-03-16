package com.patrykandpatryk.liftapp.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.patrykandpatryk.liftapp.functionality.database.di.DatabaseDateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@ProvidedTypeConverter
class LocalDateTimeConverters
@Inject
constructor(@DatabaseDateFormat private val dateFormat: SimpleDateFormat) {

    @TypeConverter
    fun toString(localDateTime: LocalDateTime): String =
        localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    @TypeConverter fun toDate(string: String) = LocalDateTime.parse(string)

    @TypeConverter
    fun toString(localDate: LocalDate): String = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

    @TypeConverter fun toLocalDateTime(string: String): LocalDate = LocalDate.parse(string)
}
