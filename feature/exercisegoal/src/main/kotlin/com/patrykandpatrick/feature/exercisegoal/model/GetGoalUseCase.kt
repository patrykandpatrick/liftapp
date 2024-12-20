package com.patrykandpatrick.feature.exercisegoal.model

import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalRouteData
import com.patrykandpatryk.liftapp.domain.goal.GetExerciseGoalContract
import com.patrykandpatryk.liftapp.domain.goal.Goal
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetGoalUseCase
@Inject
constructor(
    private val contract: GetExerciseGoalContract,
    private val routeData: ExerciseGoalRouteData,
) {
    operator fun invoke(): Flow<Goal> = contract.getGoal(routeData.routineID, routeData.exerciseID)
}
