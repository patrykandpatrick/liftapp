package com.patrykandpatryk.liftapp.feature.newroutine.model

import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exception.RoutineNotFoundException
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExerciseIDsContract
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseIds
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform

class GetRoutineWithExerciseIDsUseCase
@Inject
constructor(
    private val newRoutineRouteData: NewRoutineRouteData,
    private val contract: GetRoutineWithExerciseIDsContract,
) {

    operator fun invoke(): Flow<RoutineWithExerciseIds?> =
        if (newRoutineRouteData.routineID == ID_NOT_SET) {
            flowOf(null)
        } else {
            contract.getRoutineWithExerciseIDs(newRoutineRouteData.routineID).transform { routine ->
                if (routine == null) {
                    throw RoutineNotFoundException(newRoutineRouteData.routineID)
                } else {
                    emit(routine)
                }
            }
        }
}
