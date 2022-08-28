package com.patrykandpatryk.liftapp.domain

object Constants {

    object Logging {
        const val DISPLAYABLE_ERROR = "displayable_error"
    }

    object Database {
        const val Name = "LiftApp.db"
        const val DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ"
        const val ID_NOT_SET = 0L
    }

    object Input {

        const val INCREMENT_SHORT = 0.1f
        const val INCREMENT_LONG = 0.5f

        const val BODY_MIN_VALUE = .1f
        const val BODY_MAX_WEIGHT_METRIC = 315f
        const val BODY_MAX_WEIGHT_IMPERIAL = 700f
        const val BODY_MAX_LENGTH_METRIC = 200f
        const val BODY_MAX_LENGTH_IMPERIAL = 80f
        const val BODY_MAX_PERCENTAGE = 100f
    }
}
