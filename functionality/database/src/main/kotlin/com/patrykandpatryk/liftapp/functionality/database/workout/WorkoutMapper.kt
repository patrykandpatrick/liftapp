package com.patrykandpatryk.liftapp.functionality.database.workout

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
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
            name = workoutEntity.name,
            date = workoutEntity.date,
            duration = workoutEntity.durationMillis.milliseconds,
            notes = workoutEntity.notes,
            exercises =
                exercises.groupByExerciseAndGoal().map { (exerciseWithGoal, sets) ->
                    val goal = exerciseWithGoal.second?.toDomain() ?: exerciseWithGoal.first.goal
                    toDomain(
                        exercise = exerciseWithGoal.first,
                        goal = goal,
                        sets =
                            toDomain(
                                exerciseType = exerciseWithGoal.first.exerciseType,
                                goal = goal,
                                sets = sets,
                                massUnit = massUnit,
                                distanceUnit = distanceUnit,
                                bodyWeight = bodyWeight,
                            ),
                    )
                },
        )
    }

    private fun List<WorkoutExerciseDto>.groupByExerciseAndGoal():
        Map<Pair<ExerciseEntity, WorkoutGoalEntity?>, MutableMap<Int, ExerciseSetEntity>> =
        fold(
            mutableMapOf<
                Pair<ExerciseEntity, WorkoutGoalEntity?>,
                MutableMap<Int, ExerciseSetEntity>,
            >()
        ) { map, dto ->
            val key = dto.exercise to dto.goal
            val sets = map[key] ?: mutableMapOf()
            dto.set?.also { set -> sets[set.setIndex] = set }
            map[key] = sets
            map
        }

    private fun toDomain(
        exercise: ExerciseEntity,
        goal: Goal,
        sets: List<ExerciseSet>,
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
        )

    private fun toDomain(
        exerciseType: ExerciseType,
        goal: Goal,
        sets: Map<Int, ExerciseSetEntity>,
        massUnit: MassUnit,
        distanceUnit: LongDistanceUnit,
        bodyWeight: BodyMeasurementValue.SingleValue?,
    ): List<ExerciseSet> =
        (0 until goal.sets).map { setIndex ->
            val set = sets[setIndex]
            when (exerciseType) {
                ExerciseType.Weight ->
                    set?.toWeightSet(massUnit) ?: ExerciseSet.Weight.empty(massUnit)

                ExerciseType.Calisthenics ->
                    set?.toCalisthenicsSet(massUnit, bodyWeight)
                        ?: ExerciseSet.Calisthenics.empty(
                            bodyWeight =
                                bodyWeight?.let {
                                    converter.convert(it.unit as MassUnit, massUnit, it.value)
                                } ?: 0.0,
                            massUnit = massUnit,
                        )

                ExerciseType.Reps -> set?.toRepsSet() ?: ExerciseSet.Reps.empty

                ExerciseType.Cardio ->
                    set?.toCardioSet(distanceUnit) ?: ExerciseSet.Cardio.empty(distanceUnit)

                ExerciseType.Time -> set?.toTimeSet() ?: ExerciseSet.Time.empty
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

fun WorkoutGoalEntity.toDomain(): Goal =
    Goal(
        id = id,
        minReps = minReps,
        maxReps = maxReps,
        sets = sets,
        breakDuration = breakDurationMillis.milliseconds,
    )
