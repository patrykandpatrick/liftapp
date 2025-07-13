package com.patrykandpatrick.liftapp.navigation

import com.patrykandpatrick.liftapp.navigation.data.BodyMeasurementDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseGoalRouteData
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewBodyMeasurementRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewExerciseRouteData
import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import com.patrykandpatrick.liftapp.navigation.data.PlanConfiguratorRouteData
import com.patrykandpatrick.liftapp.navigation.data.PlanCreatorRouteData
import com.patrykandpatrick.liftapp.navigation.data.PlanListRouteData
import com.patrykandpatrick.liftapp.navigation.data.RoutineDetailsRouteData
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
        fun details(routineID: Long) = RoutineDetailsRouteData(routineID)

        fun edit(routineID: Long, deleteResultKey: String) =
            NewRoutineRouteData(routineID, deleteResultKey)

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

        fun details(bodyMeasurementID: Long) = BodyMeasurementDetailsRouteData(bodyMeasurementID)

        fun newMeasurement(bodyMeasurementID: Long, bodyMeasurementEntryID: Long = ID_NOT_SET) =
            NewBodyMeasurementRouteData(bodyMeasurementID, bodyMeasurementEntryID)
    }

    @Serializable object About

    @Serializable object Settings

    @Serializable object OneRepMax

    object Plan {
        fun edit(planID: Long) = PlanCreatorRouteData(planID)

        fun new() = PlanCreatorRouteData(ID_NOT_SET)

        fun list() = PlanListRouteData(isPickingTrainingPlan = false, resultKey = "")

        fun select(resultKey: String) =
            PlanListRouteData(isPickingTrainingPlan = true, resultKey = resultKey)

        fun configure(planID: Long) = PlanConfiguratorRouteData(planID)
    }

    object Workout {
        fun new(routineID: Long) = WorkoutRouteData(routineID)

        fun edit(workoutID: Long) = WorkoutRouteData(workoutID = workoutID)
    }
}
