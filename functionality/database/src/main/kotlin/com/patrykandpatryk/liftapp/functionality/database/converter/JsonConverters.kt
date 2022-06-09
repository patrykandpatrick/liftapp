package com.patrykandpatryk.liftapp.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementValues
import com.patrykandpatryk.liftapp.domain.model.Name
import javax.inject.Inject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class JsonConverters @Inject constructor(
    private val json: Json,
) {

    @TypeConverter
    fun toString(name: Name): String = json.encodeToString(name)

    @TypeConverter
    fun toName(string: String): Name = json.decodeFromString(string)

    @TypeConverter
    fun toString(measurementValues: MeasurementValues): String = json.encodeToString(measurementValues)

    @TypeConverter
    fun toMeasurementValues(string: String): MeasurementValues = json.decodeFromString(string)
}
