package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.runIf
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.functionality.database.workout.ExerciseSetEntity
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.first

class ExerciseSetMapper
@Inject
constructor(
    private val converter: UnitConverter,
    private val preferenceRepository: PreferenceRepository,
) {

    suspend fun mapCompletedExerciseSets(
        exerciseType: ExerciseType,
        sets: List<ExerciseSetEntity>,
    ): List<ExerciseSet> {
        val massUnit = preferenceRepository.massUnit.get().first()
        val distanceUnit = preferenceRepository.longDistanceUnit.get().first()

        return mapExerciseSets(
            exerciseType = exerciseType,
            setCount = sets.size,
            sets = sets.associateBy { it.setIndex },
            massUnit = massUnit,
            distanceUnit = distanceUnit,
            fillWithEmptySets = false,
        )
    }

    fun mapWorkoutExerciseSets(
        exerciseType: ExerciseType,
        setCount: Int,
        sets: Map<Int, ExerciseSetEntity?>,
        massUnit: MassUnit,
        distanceUnit: LongDistanceUnit,
        bodyWeight: BodyMeasurementValue.SingleValue? = null,
    ): List<ExerciseSet> =
        mapExerciseSets(
            exerciseType = exerciseType,
            setCount = setCount,
            sets = sets,
            massUnit = massUnit,
            distanceUnit = distanceUnit,
            bodyWeight = bodyWeight,
            fillWithEmptySets = true,
        )

    private fun mapExerciseSets(
        exerciseType: ExerciseType,
        setCount: Int,
        sets: Map<Int, ExerciseSetEntity?>,
        massUnit: MassUnit,
        distanceUnit: LongDistanceUnit,
        bodyWeight: BodyMeasurementValue.SingleValue? = null,
        fillWithEmptySets: Boolean,
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
