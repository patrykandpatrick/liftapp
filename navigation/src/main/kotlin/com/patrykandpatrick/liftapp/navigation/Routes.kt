package com.patrykandpatrick.liftapp.navigation

import com.patrykandpatrick.liftapp.navigation.data.NewPlanRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import com.patrykandpatrick.liftapp.navigation.data.RoutineListRouteData
import com.patrykandpatrick.liftapp.navigation.data.WorkoutRouteData
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlinx.serialization.Serializable

object Routes {
    @Serializable object Home

    object Routine {
        @Serializable class Details internal constructor(val routineID: Long)

        fun details(routineID: Long) = Details(routineID)

        fun edit(routineID: Long) = NewRoutineRouteData(routineID)

        fun list() = RoutineListRouteData(isPickingRoutine = false)

        fun new() = NewRoutineRouteData(ID_NOT_SET)

        fun pickRoutine(resultKey: String) =
            RoutineListRouteData(isPickingRoutine = true, resultKey = resultKey)
    }

    object Exercise {
        @Serializable class Create internal constructor(val exerciseID: Long = ID_NOT_SET)

        @Serializable class Details internal constructor(val exerciseID: Long)

        @Serializable
        class List
        internal constructor(
            val pickingMode: Boolean,
            val disabledExerciseIDs: kotlin.collections.List<Long>? = null,
        )

        fun details(exerciseID: Long) = Details(exerciseID)

        fun list() = List(false, null)

        fun pick(disabledExerciseIDs: kotlin.collections.List<Long>? = null) =
            List(true, disabledExerciseIDs)

        fun new() = Create()

        fun edit(exerciseID: Long) = Create(exerciseID)
    }

    object BodyMeasurement {
        @Serializable class Details internal constructor(val bodyMeasurementID: Long)

        @Serializable
        class Create
        internal constructor(val bodyMeasurementID: Long, val bodyMeasurementEntryID: Long)

        fun details(bodyMeasurementID: Long) = Details(bodyMeasurementID)

        fun newMeasurement(bodyMeasurementID: Long, bodyMeasurementEntryID: Long = ID_NOT_SET) =
            Create(bodyMeasurementID, bodyMeasurementEntryID)
    }

    @Serializable object About

    @Serializable object Settings

    @Serializable object OneRepMax

    object Plan {
        fun edit(planID: Long) = NewPlanRouteData(planID)

        fun new() = NewPlanRouteData(ID_NOT_SET)
    }

    object Workout {
        fun new(routineID: Long) = WorkoutRouteData(routineID)

        fun edit(workoutID: Long) = WorkoutRouteData(workoutID = workoutID)
    }
}
