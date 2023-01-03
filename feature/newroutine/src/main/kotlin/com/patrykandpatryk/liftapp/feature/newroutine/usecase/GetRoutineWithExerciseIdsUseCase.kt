package com.patrykandpatryk.liftapp.feature.newroutine.usecase

import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRoutineWithExerciseIdsUseCase @Inject constructor(
    private val routineRepository: RoutineRepository,
) {

    operator fun invoke(routineId: Long): Flow<Pair<Routine, List<Long>>?> =
        routineRepository.getRoutineWithExercises(routineId)
            .map { routineWithExercises ->
                if (routineWithExercises == null) return@map null
                Routine(
                    id = routineWithExercises.id,
                    name = routineWithExercises.name,
                ) to routineWithExercises
                    .exercises
                    .map { exercise -> exercise.id }
            }
}
