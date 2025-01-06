package com.patrykandpatryk.liftapp.core.deeplink

import android.net.Uri

class DeepLink private constructor(val uri: String) {

    fun createLink(vararg replacement: Any): Uri {
        var argIndex = 0
        var link = uri
        placeholderRegex.findAll(link).forEach { result ->
            link = link.replace(result.value, replacement[argIndex++].toString())
        }
        return Uri.parse(link)
    }

    companion object {
        const val SCHEME = "liftapp"
        private val placeholderRegex = """\{\w+\}""".toRegex()

        fun create(uriPattern: String): DeepLink = DeepLink("$SCHEME://$uriPattern")

        val WorkoutRoute = create("workout/{routineID}/{workoutID}")
    }
}
