package com.patrykandpatryk.liftapp.feature.newexercise.mapper

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newexercise.state.NewExerciseState
import javax.inject.Inject

class ExerciseToStateMapper @Inject constructor() : Mapper<Exercise, NewExerciseState> {

    override suspend fun map(input: Exercise): NewExerciseState =
        NewExerciseState.Valid(
            id = input.id,
            name = input.name.toValid(),
            displayName = input.displayName,
            type = input.exerciseType,
            mainMuscles = input.mainMuscles.toValid(),
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}
