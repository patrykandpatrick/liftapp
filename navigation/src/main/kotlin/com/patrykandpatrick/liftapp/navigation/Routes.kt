package com.patrykandpatrick.liftapp.navigation

import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.navigation.data.BackupRestoreRouteData
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
import com.patrykandpatrick.liftapp.navigation.data.SupersetDetailsRouteData
import com.patrykandpatrick.liftapp.navigation.data.WorkoutRouteData
import com.patrykandpatrick.liftapp.navigation.serialization.ExercisesSerializer
import kotlinx.serialization.Serializable

object Routes {
    sealed interface HomeTabRoute

    @Serializable
    object Home {
        @Serializable object Dashboard

        @Serializable object Plan

        @Serializable(ExercisesSerializer::class)
        object Exercises : ExerciseListRouteData(Mode.View, disabledExerciseIDs = null)

        @Serializable object BodyMeasurements

        @Serializable object More
    }

    /**
     * Top-level navigation graphs for the destinations shown in the bottom bar.
     *
     * Keeping these separate from [Home] lets Navigation save each tab under its own graph rather
     * than associating every destination popped above Dashboard with Dashboard itself.
     */
    object HomeTab {
        @Serializable object Dashboard : HomeTabRoute

        @Serializable object Plan : HomeTabRoute

        @Serializable object Exercises : HomeTabRoute

        @Serializable object BodyMeasurements : HomeTabRoute

        @Serializable object More : HomeTabRoute
    }

    object Routine {
        fun details(routineID: Long) = RoutineDetailsRouteData(routineID)

        fun edit(routineID: Long) = NewRoutineRouteData(routineID)

        fun list() = RoutineListRouteData(isPickingRoutine = false)

        fun new() = NewRoutineRouteData(ID_NOT_SET)

        fun pickRoutine(resultKey: String) =
            RoutineListRouteData(isPickingRoutine = true, resultKey = resultKey)

        fun superset(routineID: Long, routineItemID: Long = ID_NOT_SET) =
            SupersetDetailsRouteData(routineID, routineItemID)
    }

    object Exercise {
        fun details(exerciseID: Long) = ExerciseDetailsRouteData(exerciseID)

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

    @Serializable object Settings

    @Serializable object OpenSourceLicenses

    @Serializable
    data class OpenSourceLicense(
        val name: String,
        val offset: Int = 0,
        val length: Int = 0,
        val rawResourceId: Int? = null,
    )

    object Backup {
        @Serializable object Overview

        @Serializable object Export

        @Serializable object Automatic

        fun restore(location: String) = BackupRestoreRouteData(location)
    }

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

    @Serializable object Journal
}
