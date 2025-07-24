package com.patrykandpatryk.liftapp.functionality.database.workout

import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.runIf
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.first

class WorkoutMapper
@Inject
constructor(
    private val preferenceRepository: PreferenceRepository,
    private val converter: UnitConverter,
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
                            toDomain(
                                exerciseType = exerciseWithGoal.first.exerciseType,
                                setCount = goal.sets,
                                sets = currentAndLastSets.mapValues { it.value.first },
                                massUnit = massUnit,
                                distanceUnit = distanceUnit,
                                bodyWeight = bodyWeight,
                            ),
                        lastSets =
                            toDomain(
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

    private fun toDomain(
        exerciseType: ExerciseType,
        setCount: Int,
        sets: Map<Int, ExerciseSetEntity?>,
        massUnit: MassUnit,
        distanceUnit: LongDistanceUnit,
        bodyWeight: BodyMeasurementValue.SingleValue?,
        fillWithEmptySets: Boolean = true,
    ): List<ExerciseSet> =
        (0 until setCount).mapNotNull { setIndex ->
            val set = sets[setIndex]
            when (exerciseType) {
                ExerciseType.Weight ->
                    set?.toWeightSet(massUnit)
                        ?: runIf(fillWithEmptySets) { ExerciseSet.Weight.empty(massUnit) }

                ExerciseType.Calisthenics ->
                    set?.toCalisthenicsSet(massUnit, bodyWeight)
                        ?: runIf(fillWithEmptySets) {
                            ExerciseSet.Calisthenics.empty(
                                bodyWeight =
                                    bodyWeight?.let {
                                        converter.convert(it.unit as MassUnit, massUnit, it.value)
                                    } ?: 0.0,
                                massUnit = massUnit,
                            )
                        }

                ExerciseType.Reps ->
                    set?.toRepsSet() ?: runIf(fillWithEmptySets) { ExerciseSet.Reps.empty }

                ExerciseType.Cardio ->
                    set?.toCardioSet(distanceUnit)
                        ?: runIf(fillWithEmptySets) { ExerciseSet.Cardio.empty(distanceUnit) }

                ExerciseType.Time ->
                    set?.toTimeSet() ?: runIf(fillWithEmptySets) { ExerciseSet.Time.empty }
            }
        }

    private fun ExerciseSetEntity.toWeightSet(massUnit: MassUnit): ExerciseSet.Weight =
        ExerciseSet.Weight(
            weight = weight ?: 0.0,
            reps = reps ?: 0,
            weightUnit = weightUnit ?: massUnit,
        )

    private fun ExerciseSetEntity.toCalisthenicsSet(
        massUnit: MassUnit,
        bodyWeight: BodyMeasurementValue.SingleValue?,
    ): ExerciseSet.Calisthenics {
        val weightUnit = weightUnit ?: massUnit
        return ExerciseSet.Calisthenics(
            weight = weight ?: 0.0,
            bodyWeight =
                bodyWeight?.let { converter.convert(it.unit as MassUnit, weightUnit, it.value) }
                    ?: 0.0,
            reps = reps ?: 0,
            weightUnit = weightUnit,
        )
    }

    private fun ExerciseSetEntity.toRepsSet(): ExerciseSet.Reps = ExerciseSet.Reps(reps ?: 0)

    private fun ExerciseSetEntity.toCardioSet(distanceUnit: LongDistanceUnit): ExerciseSet.Cardio =
        ExerciseSet.Cardio(
            duration = (timeMillis ?: 0).milliseconds,
            distance = distance ?: 0.0,
            kcal = kcal ?: 0.0,
            distanceUnit = distanceUnit,
        )

    private fun ExerciseSetEntity.toTimeSet(): ExerciseSet.Time =
        ExerciseSet.Time((timeMillis ?: 0).milliseconds)
}

fun WorkoutGoalEntity.toDomain(): Workout.Goal =
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

fun Goal.toWorkoutGoal(): Workout.Goal =
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
