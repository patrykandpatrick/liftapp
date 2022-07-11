package com.patrykandpatryk.liftapp.feature.newexercise.mapper

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.feature.newexercise.state.NewExerciseState
import javax.inject.Inject

class StateToExerciseUpdateMapper @Inject constructor() : Mapper<NewExerciseState.Valid, Exercise.Update> {

    override fun map(input: NewExerciseState.Valid): Exercise.Update = Exercise.Update(
        id = input.id,
        name = input.name.value,
        mainMuscles = input.mainMuscles.value,
        secondaryMuscles = input.secondaryMuscles,
        tertiaryMuscles = input.tertiaryMuscles,
    )
}
