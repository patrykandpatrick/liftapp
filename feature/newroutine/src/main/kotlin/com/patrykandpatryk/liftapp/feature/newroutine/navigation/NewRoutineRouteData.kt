package com.patrykandpatryk.liftapp.feature.newroutine.navigation

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlinx.serialization.Serializable

@Serializable
class NewRoutineRouteData private constructor(val routineID: Long) {
    companion object {
        fun edit(routineID: Long) = NewRoutineRouteData(routineID)

        fun new() = NewRoutineRouteData(ID_NOT_SET)
    }
}
