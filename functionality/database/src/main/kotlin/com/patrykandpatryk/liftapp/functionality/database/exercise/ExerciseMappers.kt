package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import javax.inject.Inject

class ExerciseEntityToDomainMapper @Inject constructor() : Mapper<ExerciseEntity, Exercise> {

    override fun map(input: ExerciseEntity): Exercise =
        Exercise(
            id = input.id,
            name = input.name,
            exerciseType = input.exerciseType,
            mainMuscles = input.mainMuscles,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}

class ExerciseInsertToEntityMapper @Inject constructor() : Mapper<Exercise.Insert, ExerciseEntity> {

    override fun map(input: Exercise.Insert): ExerciseEntity =
        ExerciseEntity(
            name = input.name,
            exerciseType = input.exerciseType,
            mainMuscles = input.mainMuscles,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}

class ExerciseUpdateToEntityMapper @Inject constructor() : Mapper<Exercise.Update, ExerciseEntity.Update> {

    override fun map(input: Exercise.Update): ExerciseEntity.Update =
        ExerciseEntity.Update(
            id = input.id,
            name = input.name,
            mainMuscles = input.mainMuscles,
            secondaryMuscles = input.secondaryMuscles,
            tertiaryMuscles = input.tertiaryMuscles,
        )
}
