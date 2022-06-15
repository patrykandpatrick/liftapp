package com.patrykandpatryk.liftapp.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.patrykandpatryk.liftapp.functionality.database.di.DatabaseDateFormat
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@ProvidedTypeConverter
class DateConverters @Inject constructor(
    @DatabaseDateFormat private val dateFormat: SimpleDateFormat,
) {

    @TypeConverter
    fun toString(date: Date): String = dateFormat.format(date)

    @TypeConverter
    fun toDate(string: String) = dateFormat.parse(string)
}
