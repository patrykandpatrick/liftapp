package com.patrykandpatryk.liftapp.functionality.database.workout

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseSetMapper
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
                exercises.groupByExerciseAndGoal().map { (exerciseWithGoal, currentAndLastSets) ->
                    val goal =
                        exerciseWithGoal.second?.toDomain()
                            ?: exerciseWithGoal.first.goal.toWorkoutGoal()
                    toDomain(
                        exercise = exerciseWithGoal.first,
                        goal = goal,
                        sets =
                            exerciseSetMapper.mapWorkoutExerciseSets(
                                exerciseType = exerciseWithGoal.first.exerciseType,
                                setCount = goal.sets,
                                sets = currentAndLastSets.mapValues { it.value.first },
                                massUnit = massUnit,
                                distanceUnit = distanceUnit,
                                bodyWeight = bodyWeight,
                            ),
                        lastSets =
                            exerciseSetMapper.mapWorkoutExerciseSets(
                                exerciseType = exerciseWithGoal.first.exerciseType,
                                setCount = goal.sets,
                                sets = currentAndLastSets.mapValues { it.value.second },
                                massUnit = massUnit,
                                distanceUnit = distanceUnit,
                                bodyWeight = bodyWeight,
                            ),
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
                    model.map { (_, exercise, goal, currentSet, lastSet) ->
                        WorkoutExerciseDto(exercise, goal, currentSet, lastSet)
                    },
                )
            }

    private fun List<WorkoutExerciseDto>.groupByExerciseAndGoal():
        Map<
            Pair<ExerciseEntity, WorkoutGoalEntity?>,
            MutableMap<Int, Pair<ExerciseSetEntity?, ExerciseSetEntity?>>,
        > =
        fold(mutableMapOf()) { map, dto ->
            val key = dto.exercise to dto.goal
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
        exercise: ExerciseEntity,
        goal: Workout.Goal,
        sets: List<ExerciseSet>,
        lastSets: List<ExerciseSet>,
    ): Workout.Exercise =
        Workout.Exercise(
            id = exercise.id,
            name = exercise.name,
            exerciseType = exercise.exerciseType,
            mainMuscles = exercise.mainMuscles,
            secondaryMuscles = exercise.secondaryMuscles,
            tertiaryMuscles = exercise.tertiaryMuscles,
            goal = goal,
            sets = sets,
            lastSets = lastSets,
        )
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

private fun Goal.toWorkoutGoal(): Workout.Goal =
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
