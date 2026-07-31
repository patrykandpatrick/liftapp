package com.patrykandpatrick.feature.exercisegoal.model

import com.patrykandpatrick.liftapp.domain.routine.GetRoutineWithItemsUseCase
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.invoke
import com.patrykandpatrick.liftapp.navigation.data.ExerciseGoalRouteData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Tells whether the routine performs the exercise as part of a superset, which defines the set
 * count and the rest time on its own.
 */
class IsExerciseInSupersetUseCase
@Inject
constructor(
    private val getRoutineWithItems: GetRoutineWithItemsUseCase,
    private val routeData: ExerciseGoalRouteData,
) {
    operator fun invoke(): Flow<Boolean> =
        getRoutineWithItems(routeData.routineID).map { routine ->
            routine?.items.orEmpty().any { item ->
                item.type == RoutineItemType.Superset && routeData.exerciseID in item.exerciseIDs
            }
        }
}
