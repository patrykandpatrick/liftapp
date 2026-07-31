package com.patrykandpatrick.liftapp.feature.newexercise.mapper

import com.patrykandpatrick.liftapp.domain.exercise.Exercise
import com.patrykandpatrick.liftapp.domain.mapper.Mapper
import com.patrykandpatrick.liftapp.domain.validation.toValid
import com.patrykandpatrick.liftapp.feature.newexercise.model.NewExerciseState
import javax.inject.Inject

class ExerciseToStateMapper @Inject constructor() : Mapper<Exercise, NewExerciseState> {

    override suspend fun map(input: Exercise): NewExerciseState =
        NewExerciseState.Valid(
            id = input.id,
            name = input.name.toValid(),
            displayName = input.displayName,
            type = input.exerciseType,
            primaryMuscles = input.primaryMuscles.toValid(),
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}
