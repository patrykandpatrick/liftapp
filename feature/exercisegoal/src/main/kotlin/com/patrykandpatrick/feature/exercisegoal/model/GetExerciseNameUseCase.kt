package com.patrykandpatrick.feature.exercisegoal.model

import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalRouteData
import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseNameContract
import com.patrykandpatryk.liftapp.domain.model.Name
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExerciseNameUseCase
@Inject
constructor(
    private val contract: GetExerciseNameContract,
    private val routeData: ExerciseGoalRouteData,
) {
    operator fun invoke(): Flow<Name?> = contract.getExerciseName(routeData.exerciseID)
}
