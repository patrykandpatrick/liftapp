package com.patrykandpatryk.liftapp.functionality.database.routine

import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseMapper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RoutineMapper @Inject constructor(
    private val json: Json,
    private val stringProvider: StringProvider,
    private val exerciseMapper: ExerciseMapper,
) {

    fun toDomain(
        routine: RoutineEntity,
        exercises: List<ExerciseEntity>,
    ): RoutineWithExercises {

        val primaryMuscles = exercises.flattenMuscles { mainMuscles }
        val secondaryMuscles = exercises.flattenMuscles { secondaryMuscles }
        val tertiaryMuscles = exercises.flattenMuscles { tertiaryMuscles }

        secondaryMuscles.removeAll(primaryMuscles)
        tertiaryMuscles.removeAll(primaryMuscles + secondaryMuscles)

        return RoutineWithExercises(
            id = routine.id,
            name = routine.name,
            exercises = exerciseMapper.toRoutineExerciseItem(exercises),
            primaryMuscles = primaryMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
        )
    }

    fun toDomain(input: List<RoutineWithExerciseNamesView>): List<RoutineWithExerciseNames> =
        input.map { routineWithExerciseNamesView ->
            val names: List<Name> = json.decodeFromString("[${routineWithExerciseNamesView.exerciseNames}]")

            RoutineWithExerciseNames(
                id = routineWithExerciseNamesView.routine.id,
                name = routineWithExerciseNamesView.routine.name,
                exercises = names.map(stringProvider::getResolvedName),
            )
        }
}

internal fun Routine.toEntity(): RoutineEntity = RoutineEntity(
    id = id,
    name = name,
)

private inline fun List<ExerciseEntity>.flattenMuscles(
    getMuscles: ExerciseEntity.() -> List<Muscle>,
): MutableList<Muscle> =
    fold(HashSet<Muscle>()) { set, exercise ->
        set.apply { addAll(getMuscles(exercise)) }
    }.toMutableList()
