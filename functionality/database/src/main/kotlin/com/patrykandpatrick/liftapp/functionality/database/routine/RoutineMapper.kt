package com.patrykandpatrick.liftapp.functionality.database.routine

import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.routine.Routine
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExerciseNames
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.domain.routine.SupersetConfig
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseMapper
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseWithGoalDto
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class RoutineMapper
@Inject
constructor(
    private val json: Json,
    private val stringProvider: StringProvider,
    private val exerciseMapper: ExerciseMapper,
) {

    fun toDomain(
        routine: RoutineEntity,
        itemRows: List<RoutineItemExerciseDto>,
    ): RoutineWithExercises =
        toDomain(
            routine = routine,
            exercises = itemRows.map { it.toExerciseWithGoal() },
            items = itemRows.groupBy(RoutineItemExerciseDto::item).map(::toDomain),
        )

    /**
     * Maps a routine whose query does not carry its items, such as the ones scheduled by a plan.
     * Every exercise becomes an item of its own.
     */
    fun toDomainFlat(
        routine: RoutineEntity,
        exercises: List<ExerciseWithGoalDto>,
    ): RoutineWithExercises =
        toDomain(
            routine = routine,
            exercises = exercises,
            items =
                exerciseMapper.exerciseWithGoalDtoToRoutineExerciseItem(exercises).mapIndexed {
                    index,
                    exercise ->
                    RoutineItemWithExercises(
                        id = index.toLong(),
                        type = RoutineItemType.Exercise,
                        exercises = listOf(exercise),
                    )
                },
        )

    fun toDomain(input: List<RoutineWithExerciseNamesView>): List<RoutineWithExerciseNames> =
        input.map { routineWithExerciseNamesView ->
            val names: List<Name> =
                json.decodeFromString("[${routineWithExerciseNamesView.exerciseNames}]")

            RoutineWithExerciseNames(
                id = routineWithExerciseNamesView.routine.id,
                name = routineWithExerciseNamesView.routine.name,
                exercises = names.map(stringProvider::getResolvedName),
            )
        }

    private fun toDomain(
        routine: RoutineEntity,
        exercises: List<ExerciseWithGoalDto>,
        items: List<RoutineItemWithExercises>,
    ): RoutineWithExercises {
        val primaryMuscles = exercises.flattenMuscles { mainMuscles }
        val secondaryMuscles = exercises.flattenMuscles { secondaryMuscles }
        val tertiaryMuscles = exercises.flattenMuscles { tertiaryMuscles }

        secondaryMuscles.removeAll(primaryMuscles)
        tertiaryMuscles.removeAll(primaryMuscles + secondaryMuscles)

        return RoutineWithExercises(
            id = routine.id,
            name = routine.name,
            items = items,
            primaryMuscles = primaryMuscles,
            secondaryMuscles = secondaryMuscles,
            tertiaryMuscles = tertiaryMuscles,
        )
    }

    private fun toDomain(
        item: Map.Entry<RoutineItemEntity, List<RoutineItemExerciseDto>>
    ): RoutineItemWithExercises {
        val (entity, rows) = item
        val supersetConfig = rows.first().toSupersetConfig(entity.type)
        val exercises =
            exerciseMapper.exerciseWithGoalDtoToRoutineExerciseItem(
                rows.map { it.toExerciseWithGoal() }
            )
        return RoutineItemWithExercises(
            id = entity.id,
            type = entity.type,
            exercises = exercises.map { it.withSupersetConfig(supersetConfig) },
            supersetConfig = supersetConfig,
        )
    }
}

internal fun Routine.toEntity(): RoutineEntity = RoutineEntity(id = id, name = name)

private fun RoutineItemExerciseDto.toExerciseWithGoal(): ExerciseWithGoalDto =
    ExerciseWithGoalDto(exercise, goal)

private fun RoutineItemExerciseDto.toSupersetConfig(type: RoutineItemType): SupersetConfig? =
    if (type == RoutineItemType.Superset) {
        SupersetConfig(
            sets = checkNotNull(supersetSets),
            restTime = checkNotNull(supersetRestTimeMillis).milliseconds,
        )
    } else {
        null
    }

/** The set count and the rest time of a superset apply to each of its exercises. */
private fun RoutineExerciseItem.withSupersetConfig(config: SupersetConfig?): RoutineExerciseItem =
    if (config == null) {
        this
    } else {
        copy(goal = goal.copy(sets = config.sets, restTime = config.restTime))
    }

private inline fun List<ExerciseWithGoalDto>.flattenMuscles(
    getMuscles: ExerciseEntity.() -> List<Muscle>
): MutableList<Muscle> =
    fold(HashSet<Muscle>()) { set, exerciseWithGoal ->
            set.apply { addAll(getMuscles(exerciseWithGoal.exerciseEntity)) }
        }
        .toMutableList()
