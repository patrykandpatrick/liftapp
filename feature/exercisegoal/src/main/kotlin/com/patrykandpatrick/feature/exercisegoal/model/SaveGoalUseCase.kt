package com.patrykandpatrick.feature.exercisegoal.model

import com.patrykandpatrick.liftapp.navigation.data.ExerciseGoalRouteData
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.goal.SaveGoalContract
import javax.inject.Inject

class SaveGoalUseCase
@Inject
constructor(private val contract: SaveGoalContract, private val routeData: ExerciseGoalRouteData) {

    suspend operator fun invoke(goal: Goal) {
        contract.saveGoal(routeData.routineID, routeData.exerciseID, goal)
    }
}
