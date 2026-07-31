package com.patrykandpatrick.liftapp.feature.dashboard.model

import com.patrykandpatrick.liftapp.domain.date.DAYS_IN_WEEK
import com.patrykandpatrick.liftapp.domain.date.GetFirstDayOfWeekUseCase
import com.patrykandpatrick.liftapp.domain.date.atStartOfWeek
import com.patrykandpatrick.liftapp.domain.date.invoke
import com.patrykandpatrick.liftapp.domain.unit.GetPreferredMassUnitUseCase
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.unit.invoke
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutsInRangeContract
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.Duration as JavaDuration
import java.time.LocalDate
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Totals the workouts finished during the week the dashboard is showing, which is the same week the
 * day picker lays out. Sets that were never completed are left out, so a workout that was abandoned
 * partway only counts what was actually done.
 */
class GetDashboardStatisticsUseCase
@Inject
constructor(
    private val getPastWorkouts: GetPastWorkoutsInRangeContract,
    private val getPreferredMassUnit: GetPreferredMassUnitUseCase,
    private val getFirstDayOfWeek: GetFirstDayOfWeekUseCase,
    private val unitConverter: UnitConverter,
) {
    operator fun invoke(today: LocalDate = LocalDate.now()): Flow<DashboardStatistics> =
        combine(getPreferredMassUnit(), getFirstDayOfWeek(), ::Pair).flatMapLatest {
            (massUnit, firstDayOfWeek) ->
            val start = today.atStartOfWeek(firstDayOfWeek)
            getPastWorkouts.getPastWorkouts(start, start.plusDays(DAYS_IN_WEEK.toLong())).map {
                workouts ->
                workouts.fold(DashboardStatistics.empty(massUnit)) { statistics, workout ->
                    statistics.add(workout, massUnit)
                }
            }
        }

    private fun DashboardStatistics.add(workout: Workout, massUnit: MassUnit): DashboardStatistics {
        var volume = this.volume
        var reps = this.reps

        workout.exercises.forEach { exercise ->
            exercise.sets
                .filter { set -> set.isCompleted }
                .forEach { set ->
                    val setReps = set.reps ?: return@forEach
                    reps += setReps

                    val weight = set.weight
                    val weightUnit = set.weightUnit
                    if (weight != null && weightUnit != null) {
                        volume += unitConverter.convert(weightUnit, massUnit, weight) * setReps
                    }
                }
        }

        return copy(
            volume = volume,
            reps = reps,
            workouts = workouts + 1,
            timeExercised = timeExercised + workout.duration(),
        )
    }

    private fun Workout.duration(): Duration =
        endDate?.let { end -> JavaDuration.between(startDate, end).toMillis().milliseconds }
            ?: Duration.ZERO
}
