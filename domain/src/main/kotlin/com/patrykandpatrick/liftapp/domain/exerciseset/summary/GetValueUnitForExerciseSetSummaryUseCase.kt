package com.patrykandpatrick.liftapp.domain.exerciseset.summary

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSummaryType
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.unit.DurationUnit
import com.patrykandpatrick.liftapp.domain.unit.EnergyUnit
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.PaceUnit
import com.patrykandpatrick.liftapp.domain.unit.RepsUnit
import com.patrykandpatrick.liftapp.domain.unit.SpeedUnit
import com.patrykandpatrick.liftapp.domain.unit.ValueUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class GetValueUnitForExerciseSetSummaryUseCase
@Inject
constructor(private val preferenceRepository: PreferenceRepository) {
    suspend operator fun invoke(type: ExerciseSummaryType): ValueUnit? =
        when (type) {
            ExerciseSummaryType.OneRepMax,
            ExerciseSummaryType.TotalVolume -> getMassUnit()
            ExerciseSummaryType.TotalDistance -> getLongDistanceUnit()
            ExerciseSummaryType.TotalDuration -> DurationUnit
            ExerciseSummaryType.AvgPace -> getPaceUnit()
            ExerciseSummaryType.AvgSpeed -> getSpeedUnit()
            ExerciseSummaryType.TotalCalories -> EnergyUnit.KiloCalorie
            ExerciseSummaryType.TotalReps -> RepsUnit
        }

    private suspend fun getLongDistanceUnit(): LongDistanceUnit =
        preferenceRepository.longDistanceUnit.get().first()

    private suspend fun getPaceUnit(): PaceUnit =
        when (getLongDistanceUnit()) {
            LongDistanceUnit.Kilometer -> PaceUnit.DurationPerKilometer
            LongDistanceUnit.Mile -> PaceUnit.DurationPerMile
        }

    private suspend fun getSpeedUnit(): SpeedUnit =
        when (getLongDistanceUnit()) {
            LongDistanceUnit.Kilometer -> SpeedUnit.KilometersPerHour
            LongDistanceUnit.Mile -> SpeedUnit.MilesPerHour
        }

    private suspend fun getMassUnit() = preferenceRepository.massUnit.get().first()
}
