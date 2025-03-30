package com.patrykandpatryk.liftapp.feature.newexercise.mapper

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.newexercise.model.NewExerciseState
import javax.inject.Inject

class StateToExerciseUpdateMapper @Inject constructor() :
    Mapper<NewExerciseState.Valid, Exercise.Update> {

    override suspend fun map(input: NewExerciseState.Valid): Exercise.Update =
        Exercise.Update(
            id = input.id,
            name = input.name.value,
            mainMuscles = input.primaryMuscles.value,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}
