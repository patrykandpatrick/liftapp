package com.patrykandpatryk.liftapp.functionality.database.routine

import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RoutineWithExerciseNamesToDomainMapper @Inject constructor(
    private val nameResolver: NameResolver,
    private val json: Json,
) : Mapper<RoutineWithExerciseNamesView, RoutineWithExerciseNames> {

    override suspend fun map(input: RoutineWithExerciseNamesView): RoutineWithExerciseNames {
        val names: List<Name> = json.decodeFromString("[${input.exerciseNames}]")

        return RoutineWithExerciseNames(
            id = input.routine.id,
            name = input.routine.name,
            exercises = names.map(nameResolver::getResolvedString),
        )
    }
}

internal class RoutineWithExercisesToDomainMapper @Inject constructor(
    private val exerciseMapper: Mapper<ExerciseEntity, Exercise>,
) : Mapper<RoutineWithExerciseEntities, RoutineWithExercises> {

    override suspend fun map(input: RoutineWithExerciseEntities): RoutineWithExercises {

        val (routine, exercises) = input

        return RoutineWithExercises(
            id = routine.id,
            name = routine.name,
            exercises = exerciseMapper(exercises),
        )
    }
}

class RoutineToEntityMapper @Inject constructor() : Mapper<Routine, RoutineEntity> {

    override suspend fun map(input: Routine): RoutineEntity = RoutineEntity(
        id = input.id,
        name = input.name,
    )
}

class RoutineToDomainMapper @Inject constructor() : Mapper<RoutineEntity, Routine> {

    override suspend fun map(input: RoutineEntity): Routine = Routine(
        id = input.id,
        name = input.name,
    )
}
