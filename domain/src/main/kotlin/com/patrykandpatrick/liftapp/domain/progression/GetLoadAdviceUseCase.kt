package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.PlanSchedule
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * What to say about the load of each set as a workout opens, keyed by exercise ID and then by set
 * index. A set with nothing to say is absent, and so is an exercise with no set that has.
 *
 * A set the plan gives a percentage of one-rep max gets its load from that. Otherwise only the
 * first set gets advice, from the progression loop; the sets after it carry its weight forward.
 *
 * Only a workout still in progress gets advice, and only for exercises with a weight to set. The
 * history stops at the workout's start, so logging its sets never moves the advice given for them.
 */
class GetLoadAdviceUseCase
@Inject
constructor(
    private val suggestLoad: SuggestLoadUseCase,
    private val getRecentSessions: GetRecentSessionsContract,
    private val getActivePlan: GetActivePlanUseCase,
    private val preferenceRepository: PreferenceRepository,
) {
    suspend operator fun invoke(workout: Workout): Map<Long, Map<Int, LoadAdvice>> {
        if (workout.isCompleted) return emptyMap()

        val massUnit = preferenceRepository.massUnit.get().first()
        val isDeload = deloadCalendar()
        val isDeloadWeek = isDeload(workout.startDate.toLocalDate())

        return workout.exercises
            .filter { exercise -> exercise.exerciseType in LOADED_TYPES }
            .mapNotNull { exercise ->
                if (exercise.goal.setTargets.isEmpty()) return@mapNotNull null
                val history =
                    getRecentSessions
                        .getRecentSessions(
                            exerciseID = exercise.id,
                            before = workout.startDate,
                            limit = SuggestLoadUseCase.HISTORY_LENGTH,
                        )
                        .map { group ->
                            ExerciseSession(
                                sets = group.sets,
                                isDeload = isDeload(group.workoutStartDate.toLocalDate()),
                            )
                        }
                val advice =
                    exercise.goal.setTargets
                        .mapIndexedNotNull { index, target ->
                            adviceFor(exercise, index, target, history, massUnit, isDeloadWeek)
                                ?.let { index to it }
                        }
                        .toMap()
                advice.takeIf { it.isNotEmpty() }?.let { exercise.id to it }
            }
            .toMap()
    }

    private fun adviceFor(
        exercise: Workout.Exercise,
        index: Int,
        target: SetTarget,
        history: List<ExerciseSession>,
        massUnit: MassUnit,
        isDeloadWeek: Boolean,
    ): LoadAdvice? {
        val percent = target.loadPercent
        val policy = exercise.progressionPolicy
        return when {
            percent != null ->
                suggestLoad.forPercent(history, percent, exercise.loadIncrement, massUnit)
                    ?: LoadAdvice.OneMoreSession.takeIf {
                        suggestLoad.isOneSessionShort(history, policy, isPercent = true)
                    }
            index == 0 ->
                suggestLoad(
                    history = history,
                    target = target,
                    policy = policy,
                    loadIncrement = exercise.loadIncrement,
                    massUnit = massUnit,
                    isDeloadWeek = isDeloadWeek,
                )
                    ?: LoadAdvice.OneMoreSession.takeIf {
                        suggestLoad.isOneSessionShort(history, policy)
                    }
            else -> null
        }
    }

    /** Placed on the active plan's calendar. With no active plan, no week is a deload. */
    private suspend fun deloadCalendar(): (LocalDate) -> Boolean {
        val (activePlan, plan) = getActivePlan().first() ?: return { false }
        val schedule =
            PlanSchedule(
                startDate = activePlan.startDate,
                itemCount = plan.items.size,
                cycleCount = activePlan.cycleCount,
            )
        return { date ->
            schedule.positionOf(date)?.let { activePlan.mesocycle.isDeload(it.cycleIndex) } == true
        }
    }

    private companion object {
        val LOADED_TYPES = setOf(ExerciseType.Weight, ExerciseType.Calisthenics)
    }
}
