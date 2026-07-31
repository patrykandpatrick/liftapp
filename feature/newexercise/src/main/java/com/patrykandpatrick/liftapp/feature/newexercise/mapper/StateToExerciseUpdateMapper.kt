package com.patrykandpatrick.liftapp.feature.newexercise.mapper

import com.patrykandpatrick.liftapp.domain.exercise.Exercise
import com.patrykandpatrick.liftapp.domain.mapper.Mapper
import com.patrykandpatrick.liftapp.feature.newexercise.model.NewExerciseState
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
