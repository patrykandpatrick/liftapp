package com.patrykandpatrick.liftapp.feature.workout.navigation

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlinx.serialization.Serializable

@Serializable
class WorkoutRouteData
internal constructor(val routineID: Long = ID_NOT_SET, val workoutID: Long = ID_NOT_SET) {
    companion object {
        fun new(routineID: Long) = WorkoutRouteData(routineID)

        fun edit(workoutID: Long) = WorkoutRouteData(workoutID = workoutID)
    }
}
