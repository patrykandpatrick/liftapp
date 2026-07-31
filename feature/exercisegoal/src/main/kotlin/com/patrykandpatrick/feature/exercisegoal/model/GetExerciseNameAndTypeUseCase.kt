package com.patrykandpatrick.feature.exercisegoal.model

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseNameAndType
import com.patrykandpatrick.liftapp.domain.exercise.GetExerciseNameAndTypeContract
import com.patrykandpatrick.liftapp.navigation.data.ExerciseGoalRouteData
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
