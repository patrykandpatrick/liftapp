package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R

const val LIST_SEPARATOR = ", "

private fun String.prepForConversionToNumber() =
    replace(oldChar = ',', newChar = '.').replace(oldValue = " ", newValue = "")

fun String.smartToIntOrNull() = prepForConversionToNumber().toIntOrNull()

fun String.smartToFloatOrNull() = prepForConversionToNumber().toFloatOrNull()

inline fun <T> Collection<T>.joinToPrettyString(
    andText: CharSequence,
    toString: (T) -> String,
): String {
    val builder = StringBuilder()

    for ((index, element) in withIndex()) {
        builder.append(toString(element))

        if (index < size - 1) {
            if (index == size - 2) {
                builder.append(" $andText ")
            } else {
                builder.append(LIST_SEPARATOR)
            }
        }
    }

    return builder.toString()
}

@Composable
inline fun <T> Collection<T>.joinToPrettyString(toString: @Composable (T) -> String): String = joinToPrettyString(
    andText = stringResource(id = R.string.and_in_a_list),
    toString = { toString(it) },
)

val String?.nonBlankOrNull: String?
    get() = this?.takeIf { it.isNotBlank() }
