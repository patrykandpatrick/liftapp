package com.patrykandpatryk.liftapp.feature.newexercise.mapper

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.newexercise.model.NewExerciseState
import javax.inject.Inject

class StateToExerciseInsertMapper @Inject constructor() :
    Mapper<NewExerciseState.Valid, Exercise.Insert> {

    override suspend fun map(input: NewExerciseState.Valid): Exercise.Insert =
        Exercise.Insert(
            name = input.name.value,
            exerciseType = input.type,
            mainMuscles = input.mainMuscles.value,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}
