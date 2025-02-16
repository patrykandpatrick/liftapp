package com.patrykandpatryk.liftapp.feature.newroutine.model

import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.UpsertRoutineWithExerciseIdsContract
import com.patrykandpatryk.liftapp.feature.newroutine.navigation.NewRoutineRouteData
import javax.inject.Inject

class UpsertRoutineUseCase
@Inject
constructor(
    private val newRoutineRouteData: NewRoutineRouteData,
    private val contract: UpsertRoutineWithExerciseIdsContract,
) {

    suspend operator fun invoke(name: String, exerciseIds: List<Long>): Long =
        contract.upsert(
            routine = Routine(id = newRoutineRouteData.routineID, name = name),
            exerciseIds = exerciseIds,
        )
}
