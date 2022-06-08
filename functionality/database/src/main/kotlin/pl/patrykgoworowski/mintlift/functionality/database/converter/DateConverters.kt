package pl.patrykgoworowski.mintlift.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
import pl.patrykgoworowski.mintlift.functionality.database.di.DatabaseDateFormat

@ProvidedTypeConverter
class DateConverters @Inject constructor(
    @DatabaseDateFormat private val dateFormat: SimpleDateFormat,
) {

    @TypeConverter
    fun toString(date: Date): String = dateFormat.format(date)

    @TypeConverter
    fun toDate(string: String) = dateFormat.parse(string)
}
