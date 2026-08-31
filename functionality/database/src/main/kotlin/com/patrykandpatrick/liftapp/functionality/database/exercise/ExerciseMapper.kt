package com.patrykandpatrick.liftapp.functionality.database.exercise

import com.patrykandpatrick.liftapp.domain.exercise.Exercise
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseNameAndType
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import javax.inject.Inject

class ExerciseMapper @Inject constructor(private val stringProvider: StringProvider) {

    fun toDomain(exercise: ExerciseEntity): Exercise =
        Exercise(
            id = exercise.id,
            displayName = stringProvider.getResolvedName(exercise.name),
            name = exercise.name,
            exerciseType = exercise.exerciseType,
            primaryMuscles = exercise.mainMuscles,
            secondaryMuscles = exercise.secondaryMuscles,
            tertiaryMuscles = exercise.tertiaryMuscles,
            role = exercise.role,
            progressionPolicy = exercise.progressionPolicy,
            loadIncrement = exercise.loadIncrement,
        )

    fun toDomain(exercises: List<ExerciseEntity>): List<Exercise> = exercises.map(::toDomain)

    fun toDomain(dto: ExerciseNameAndTypeDto): ExerciseNameAndType =
        ExerciseNameAndType(name = dto.name, type = dto.type)

    fun exerciseEntityToRoutineExerciseItem(
        exercises: List<ExerciseEntity>
    ): List<RoutineExerciseItem> = exercises.map { exercise ->
        RoutineExerciseItem(
            id = exercise.id,
            name = stringProvider.getResolvedName(exercise.name),
            muscles = stringProvider.getMuscleList(exercise.mainMuscles),
            type = exercise.exerciseType,
            role = exercise.role,
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
        role = role,
        progressionPolicy = progressionPolicy,
        loadIncrement = loadIncrement,
    )

internal fun List<Exercise.Insert>.toEntity(): List<ExerciseEntity> = map { it.toEntity() }

internal fun Exercise.Update.toEntity(): ExerciseEntity.Update =
    ExerciseEntity.Update(
        id = id,
        name = name,
        exerciseType = exerciseType,
        mainMuscles = mainMuscles,
        secondaryMuscles = secondaryMuscles,
        tertiaryMuscles = tertiaryMuscles,
        role = role,
        progressionPolicy = progressionPolicy,
    )
