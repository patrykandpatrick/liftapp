package com.patrykandpatrick.liftapp.navigation

import com.patrykandpatrick.liftapp.navigation.data.BodyMeasurementDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseGoalRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewExerciseRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewPlanRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import com.patrykandpatrick.liftapp.navigation.data.RoutineListRouteData
import com.patrykandpatrick.liftapp.navigation.data.WorkoutRouteData
import com.patrykandpatrick.liftapp.navigation.serialization.ExercisesSerializer
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlinx.serialization.Serializable

object Routes {
    @Serializable
    object Home {
        @Serializable object Dashboard

        @Serializable object Plan

        @Serializable(ExercisesSerializer::class)
        object Exercises : ExerciseListRouteData(Mode.View, disabledExerciseIDs = null)

        @Serializable object BodyMeasurements

        @Serializable object More
    }

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
        fun details(exerciseID: Long) = ExerciseDetailsRouteData(exerciseID)

        fun list() =
            ExerciseListRouteData(
                mode = ExerciseListRouteData.Mode.View,
                disabledExerciseIDs = null,
            )

        fun pick(resultKey: String, disabledExerciseIDs: List<Long>? = null) =
            ExerciseListRouteData(
                mode = ExerciseListRouteData.Mode.Pick(resultKey),
                disabledExerciseIDs = disabledExerciseIDs,
            )

        fun new() = NewExerciseRouteData(ID_NOT_SET)

        fun edit(exerciseID: Long) = NewExerciseRouteData(exerciseID)

        fun goal(routineID: Long, exerciseID: Long) = ExerciseGoalRouteData(routineID, exerciseID)
    }

    object BodyMeasurement {

        @Serializable
        class Create
        internal constructor(val bodyMeasurementID: Long, val bodyMeasurementEntryID: Long)

        fun details(bodyMeasurementID: Long) = BodyMeasurementDetailsRouteData(bodyMeasurementID)

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
