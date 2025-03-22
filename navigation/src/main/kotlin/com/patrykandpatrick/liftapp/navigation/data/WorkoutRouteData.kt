package com.patrykandpatrick.liftapp.navigation.data

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlinx.serialization.Serializable

@Serializable
class WorkoutRouteData
internal constructor(val routineID: Long = ID_NOT_SET, val workoutID: Long = ID_NOT_SET)
