package com.patrykandpatrick.liftapp.functionality.database.workout

import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseSetMapper
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.first

class WorkoutMapper
@Inject
constructor(
    private val preferenceRepository: PreferenceRepository,
    private val exerciseSetMapper: ExerciseSetMapper,
) {
    suspend fun toDomain(
        workoutEntity: WorkoutEntity,
        exercises: List<WorkoutExerciseDto>,
    ): Workout {
        val massUnit = preferenceRepository.massUnit.get().first()
        val distanceUnit = preferenceRepository.longDistanceUnit.get().first()
        val bodyWeight = workoutEntity.bodyWeight as? BodyMeasurementValue.SingleValue

        return Workout(
            id = workoutEntity.id,
            routineID = workoutEntity.routineID,
            name = workoutEntity.name,
            startDate = workoutEntity.startDate,
            endDate = workoutEntity.endDate,
            notes = workoutEntity.notes,
            exercises =
                exercises.groupByExercise().map { (exercise, currentAndLastSets) ->
                    val goal =
                        (exercise.goal?.toDomain() ?: exercise.exercise.goal.toWorkoutGoal())
                            .withSupersetConfig(exercise.item)
                    val mapSets = { sets: Map<Int, ExerciseSetEntity?> ->
                        exerciseSetMapper.mapWorkoutExerciseSets(
                            exerciseType = exercise.exercise.exerciseType,
                            setCount = goal.sets,
                            sets = sets,
                            massUnit = massUnit,
                            distanceUnit = distanceUnit,
                            bodyWeight = bodyWeight,
                        )
                    }
                    toDomain(
                        exercise = exercise,
                        goal = goal,
                        sets = mapSets(currentAndLastSets.mapValues { it.value.first }),
                        lastSets = mapSets(currentAndLastSets.mapValues { it.value.second }),
                    )
                },
        )
    }

    suspend fun toDomain(workout: List<WorkoutWithWorkoutExerciseDto>): List<Workout> =
        workout
            .groupBy { it.workout }
            .map { (workout, model) ->
                toDomain(
                    workout,
                    model.mapNotNull(WorkoutWithWorkoutExerciseDto::toWorkoutExerciseDtoOrNull),
                )
            }

    /** Collects the current and the previous set of every exercise, keyed by the set index. */
    private fun List<WorkoutExerciseDto>.groupByExercise():
        Map<ExerciseKey, MutableMap<Int, Pair<ExerciseSetEntity?, ExerciseSetEntity?>>> =
        fold(mutableMapOf()) { map, dto ->
            val key = ExerciseKey(dto.item, dto.exerciseOrder, dto.exercise, dto.goal, dto.notes)
            val sets = map[key] ?: mutableMapOf()

            dto.currentExerciseSet?.also { set ->
                sets[set.setIndex] = set to sets[set.setIndex]?.second
            }
            dto.lastExerciseSet?.also { set ->
                sets[set.setIndex] = sets[set.setIndex]?.first to set
            }

            map[key] = sets
            map
        }

    private fun toDomain(
        exercise: ExerciseKey,
        goal: Workout.Goal,
        sets: List<ExerciseSet>,
        lastSets: List<ExerciseSet>,
    ): Workout.Exercise =
        Workout.Exercise(
            id = exercise.exercise.id,
            workoutItemID = exercise.item.id,
            workoutItemType = exercise.item.type,
            workoutItemOrder = exercise.item.orderIndex,
            exerciseOrder = exercise.exerciseOrder,
            name = exercise.exercise.name,
            exerciseType = exercise.exercise.exerciseType,
            mainMuscles = exercise.exercise.mainMuscles,
            secondaryMuscles = exercise.exercise.secondaryMuscles,
            tertiaryMuscles = exercise.exercise.tertiaryMuscles,
            goal = goal,
            notes = exercise.notes,
            sets = sets,
            lastSets = lastSets,
        )

    /** Identifies one exercise of one workout item, which the query returns once per set. */
    private data class ExerciseKey(
        val item: WorkoutItemEntity,
        val exerciseOrder: Int,
        val exercise: ExerciseEntity,
        val goal: WorkoutGoalEntity?,
        val notes: String,
    )
}

internal fun WorkoutWithWorkoutExerciseDto.toWorkoutExerciseDtoOrNull(): WorkoutExerciseDto? {
    val item = item ?: return null
    val exercise = exercise ?: return null
    val exerciseOrder = exerciseOrder ?: return null
    return WorkoutExerciseDto(
        item = item,
        exercise = exercise,
        goal = goal,
        exerciseOrder = exerciseOrder,
        notes = notes.orEmpty(),
        currentExerciseSet = currentExerciseSet,
        lastExerciseSet = lastExerciseSet,
    )
}

/** The set count and the rest time of a superset apply to each of its exercises. */
private fun Workout.Goal.withSupersetConfig(item: WorkoutItemEntity): Workout.Goal =
    if (item.type == RoutineItemType.Superset) {
        copy(
            sets = checkNotNull(item.sets),
            restTime = checkNotNull(item.restTimeMillis).milliseconds,
        )
    } else {
        this
    }

private fun WorkoutGoalEntity.toDomain(): Workout.Goal =
    Workout.Goal(
        id = id,
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        restTime = restTimeMillis.milliseconds,
        duration = durationMillis.milliseconds,
        distance = distance,
        distanceUnit = distanceUnit,
        calories = calories,
    )

internal fun Goal.toWorkoutGoal(): Workout.Goal =
    Workout.Goal(
        id = ID_NOT_SET,
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        restTime = restTime,
        duration = duration,
        distance = distance,
        distanceUnit = distanceUnit,
        calories = calories,
    )
