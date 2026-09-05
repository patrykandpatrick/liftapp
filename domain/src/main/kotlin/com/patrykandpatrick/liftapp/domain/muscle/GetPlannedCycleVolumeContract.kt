package com.patrykandpatrick.liftapp.domain.muscle

import kotlinx.coroutines.flow.Flow

fun interface GetPlannedCycleVolumeContract {
    fun getPlannedCycleVolume(planID: Long, cycleIndex: Int): Flow<List<PlannedExerciseVolume>>
}
