package com.patrykandpatrick.feature.exercisegoal.model

import com.patrykandpatrick.feature.exercisegoal.navigation.ExerciseGoalRouteData
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseNameAndType
import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseNameAndTypeContract
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExerciseNameAndTypeUseCase
@Inject
constructor(
    private val contract: GetExerciseNameAndTypeContract,
    private val routeData: ExerciseGoalRouteData,
) {
    operator fun invoke(): Flow<ExerciseNameAndType?> =
        contract.getExerciseNameAndType(routeData.exerciseID)
}
