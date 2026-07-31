package com.patrykandpatrick.liftapp.functionality.backup.file

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import java.io.Reader
import java.io.Writer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Reads and writes the DataStore preferences as CSV.
 *
 * DataStore is untyped once the keys are erased, so each row carries the type alongside the value;
 * without it there would be no way to tell the string `"1"` from the int `1` on the way back in.
 */
internal object PreferencesCsv {

    private const val TYPE_BOOLEAN = "boolean"
    private const val TYPE_INT = "int"
    private const val TYPE_LONG = "long"
    private const val TYPE_FLOAT = "float"
    private const val TYPE_DOUBLE = "double"
    private const val TYPE_STRING = "string"
    private const val TYPE_STRING_SET = "string_set"

    private val stringSetSerializer = SetSerializer(String.serializer())

    fun write(preferences: Preferences, writer: Writer, json: Json) {
        Csv.write(
            writer,
            sequence {
                yield(listOf("key", "type", "value"))
                preferences.asMap().forEach { (key, value) ->
                    if (key.name in DEVICE_SPECIFIC_KEYS) return@forEach
                    encode(value, json)?.let { (type, encoded) ->
                        yield(listOf(key.name, type, encoded))
                    } ?: Timber.w("Not backing up preference `${key.name}`: unsupported type.")
                }
            },
        )
    }

    fun read(reader: Reader, json: Json): (MutablePreferences) -> Unit {
        val rows = Csv.read(reader).iterator()
        if (!rows.hasNext()) return {}
        rows.next() // The header row.

        val entries = buildList {
            rows.forEach { row ->
                val name = row.getOrNull(0)
                val type = row.getOrNull(1)
                val value = row.getOrNull(2)
                if (name == null || type == null || value == null) {
                    Timber.w("Skipping a malformed preference row.")
                } else if (name in DEVICE_SPECIFIC_KEYS) {
                    Timber.i("Skipping device-specific preference `$name`.")
                } else {
                    decode(name, type, value, json)?.let(::add)
                        ?: Timber.w("Skipping preference `$name`: unsupported type `$type`.")
                }
            }
        }

        return { preferences -> entries.forEach { it(preferences) } }
    }

    private fun encode(value: Any, json: Json): Pair<String, String>? =
        when (value) {
            is Boolean -> TYPE_BOOLEAN to value.toString()
            is Int -> TYPE_INT to value.toString()
            is Long -> TYPE_LONG to value.toString()
            is Float -> TYPE_FLOAT to value.toString()
            is Double -> TYPE_DOUBLE to value.toString()
            is String -> TYPE_STRING to value
            is Set<*> ->
                TYPE_STRING_SET to
                    json.encodeToString(
                        stringSetSerializer,
                        value.filterIsInstance<String>().toSet(),
                    )
            else -> null
        }

    private fun decode(
        name: String,
        type: String,
        value: String,
        json: Json,
    ): ((MutablePreferences) -> Unit)? =
        when (type) {
            TYPE_BOOLEAN -> { it ->
                it[booleanPreferencesKey(name)] = value.toBoolean()
            }
            TYPE_INT -> { it ->
                it[intPreferencesKey(name)] = value.toInt()
            }
            TYPE_LONG -> { it ->
                it[longPreferencesKey(name)] = value.toLong()
            }
            TYPE_FLOAT -> { it ->
                it[floatPreferencesKey(name)] = value.toFloat()
            }
            TYPE_DOUBLE -> { it ->
                it[doublePreferencesKey(name)] = value.toDouble()
            }
            TYPE_STRING -> { it ->
                it[stringPreferencesKey(name)] = value
            }
            TYPE_STRING_SET -> { it ->
                it[stringSetPreferencesKey(name)] =
                    json.decodeFromString(stringSetSerializer, value)
            }
            else -> null
        }

    private val DEVICE_SPECIFIC_KEYS = setOf("auto_backup", "last_export_destination")
}
