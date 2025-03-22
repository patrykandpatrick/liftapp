package com.patrykandpatrick.liftapp.navigation.data

import kotlinx.serialization.Serializable

@Serializable class RoutineListRouteData(val isPickingRoutine: Boolean, val resultKey: String = "")
