package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.extension.joinToPrettyString

private fun String.prepForConversionToNumber() =
    replace(oldChar = ',', newChar = '.').replace(oldValue = " ", newValue = "")

fun String.smartToIntOrNull() = prepForConversionToNumber().toIntOrNull()

fun String.smartToFloatOrNull() = prepForConversionToNumber().toFloatOrNull()

@Composable
inline fun <T> Collection<T>.joinToPrettyString(toString: @Composable (T) -> String): String =
    joinToPrettyString(
        andText = stringResource(id = R.string.and_in_a_list),
        toString = { toString(it) },
    )

val String?.nonBlankOrNull: String?
    get() = this?.takeIf(String::isNotBlank)
