package com.patrykandpatrick.liftapp.navigation.data

import kotlinx.serialization.Serializable

@Serializable
class PlanTargetsRouteData(val planID: Long, val cycleIndex: Int, val planItemOrderIndex: Int)
