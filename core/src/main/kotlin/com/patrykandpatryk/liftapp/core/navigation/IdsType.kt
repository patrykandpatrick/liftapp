@file:Suppress("DEPRECATION", "UNCHECKED_CAST")

package com.patrykandpatryk.liftapp.core.navigation

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.Serializable

object IdsType : NavType<List<Long>>(
    isNullableAllowed = true,
) {
    override fun get(bundle: Bundle, key: String): List<Long>? =
        bundle.getSerializable(key) as? List<Long>

    override fun parseValue(value: String): List<Long> =
        Json.decodeFromString(value)

    override fun put(bundle: Bundle, key: String, value: List<Long>) {
        bundle.putSerializable(key, value as Serializable)
    }
}
