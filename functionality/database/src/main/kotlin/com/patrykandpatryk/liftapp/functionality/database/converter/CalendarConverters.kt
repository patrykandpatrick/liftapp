package com.patrykandpatryk.liftapp.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.patrykandpatryk.liftapp.domain.date.parseToCalendar
import com.patrykandpatryk.liftapp.functionality.database.di.DatabaseDateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject

@ProvidedTypeConverter
class CalendarConverters @Inject constructor(
    @DatabaseDateFormat private val dateFormat: SimpleDateFormat,
) {

    @TypeConverter
    fun toString(calendar: Calendar): String = dateFormat.format(calendar.timeInMillis)

    @TypeConverter
    fun toDate(string: String) = dateFormat.parseToCalendar(string)
}
