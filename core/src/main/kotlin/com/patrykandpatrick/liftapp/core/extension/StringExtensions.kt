package com.patrykandpatrick.liftapp.core.extension

import android.icu.text.ListFormatter
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

private fun String.prepForConversionToNumber() =
    replace(oldChar = ',', newChar = '.').replace(oldValue = " ", newValue = "")

fun String.smartToIntOrNull() = prepForConversionToNumber().toIntOrNull()

fun String.smartToFloatOrNull() = prepForConversionToNumber().toFloatOrNull()

fun String.smartToDoubleOrNull() = prepForConversionToNumber().toDoubleOrNull()

@Composable
inline fun <T : Any> Collection<T>.joinToPrettyString(
    toString: @Composable (T) -> String = { it.toString() }
): String = joinToPrettyStringIndexed { _, item -> toString(item) }

@Composable
inline fun <T : Any> Collection<T>.joinToPrettyStringIndexed(
    toString: @Composable (index: Int, item: T) -> String
): String {
    val locale = LocalConfiguration.current.locales[0]
    val strings = ArrayList<String>(size)
    for ((index, item) in withIndex()) {
        strings += toString(index, item)
    }
    return ListFormatter.getInstance(locale).format(strings)
}

val String?.nonBlankOrNull: String?
    get() = this?.takeIf(String::isNotBlank)
