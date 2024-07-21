package com.patrykandpatrick.liftapp.navigation

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlinx.serialization.Serializable

object Routes {
    @Serializable
    object Home

    object Routine {
        @Serializable
        class Create internal constructor(val routineID: Long = ID_NOT_SET)

        @Serializable
        class Details internal constructor(val routineID: Long)

        fun details(routineID: Long) = Details(routineID)
        fun new() = Create()
        fun edit(routineID: Long) = Create(routineID)
    }

    object Exercise {
        @Serializable
        class Create internal constructor(val exerciseID: Long = ID_NOT_SET)

        @Serializable
        class Details internal constructor(val exerciseID: Long)

        @Serializable
        class List internal constructor(
            val pickingMode: Boolean,
            val disabledExerciseIDs: kotlin.collections.List<Long>? = null,
        )

        fun details(exerciseID: Long) = Details(exerciseID)

        fun list() = List(false, null)

        fun pick(disabledExerciseIDs: kotlin.collections.List<Long>? = null) = List(true, disabledExerciseIDs)

        fun new() = Create()

        fun edit(exerciseID: Long) = Create(exerciseID)
    }

    object BodyMeasurement {
        @Serializable
        class Details internal constructor(val bodyMeasurementID: Long)

        @Serializable
        class Create internal constructor(val bodyMeasurementID: Long, val bodyMeasurementEntryID: Long)

        fun details(bodyMeasurementID: Long) = Details(bodyMeasurementID)

        fun newMeasurement(bodyMeasurementID: Long, bodyMeasurementEntryID: Long = ID_NOT_SET) =
            Create(bodyMeasurementID, bodyMeasurementEntryID)
    }

    @Serializable
    object About

    @Serializable
    object Settings

    @Serializable
    object OneRepMax
}
