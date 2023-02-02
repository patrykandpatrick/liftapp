package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.extension.joinToPrettyString
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import javax.inject.Inject

class ExerciseMapper @Inject constructor(
    private val stringProvider: StringProvider,
) {

    fun toDomain(exercise: ExerciseEntity): Exercise =
        Exercise(
            id = exercise.id,
            displayName = stringProvider.getResolvedName(exercise.name),
            name = exercise.name,
            exerciseType = exercise.exerciseType,
            mainMuscles = exercise.mainMuscles,
            secondaryMuscles = exercise.secondaryMuscles,
            tertiaryMuscles = exercise.tertiaryMuscles,
        )

    fun toDomain(exercises: List<ExerciseEntity>): List<Exercise> =
        exercises.map(::toDomain)

    fun toRoutineExerciseItem(exercises: List<ExerciseEntity>): List<RoutineExerciseItem> =
        exercises.map { input ->
            RoutineExerciseItem(
                id = input.id,
                name = stringProvider.getResolvedName(input.name),
                muscles = input
                    .mainMuscles
                    .joinToPrettyString(
                        andText = stringProvider.andInAList,
                        toString = stringProvider::getMuscleName,
                    ),
                type = input.exerciseType,
            )
        }
}

internal fun Exercise.Insert.toEntity(): ExerciseEntity =
    ExerciseEntity(
        name = name,
        exerciseType = exerciseType,
        mainMuscles = mainMuscles,
        secondaryMuscles = secondaryMuscles,
        tertiaryMuscles = tertiaryMuscles,
    )

internal fun List<Exercise.Insert>.toEntity(): List<ExerciseEntity> =
    map { it.toEntity() }

internal fun Exercise.Update.toEntity(): ExerciseEntity.Update =
    ExerciseEntity.Update(
        id = id,
        name = name,
        mainMuscles = mainMuscles,
        secondaryMuscles = secondaryMuscles,
        tertiaryMuscles = tertiaryMuscles,
    )
