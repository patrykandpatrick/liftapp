package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.PlanSchedule
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * What to say about each exercise's load as a workout opens, keyed by exercise ID. An exercise with
 * nothing to say is absent.
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
    suspend operator fun invoke(workout: Workout): Map<Long, LoadAdvice> {
        if (workout.isCompleted) return emptyMap()

        val massUnit = preferenceRepository.massUnit.get().first()
        val isDeload = deloadCalendar()
        val isDeloadWeek = isDeload(workout.startDate.toLocalDate())

        return workout.exercises
            .filter { exercise -> exercise.exerciseType in LOADED_TYPES }
            .mapNotNull { exercise ->
                val target = exercise.goal.setTargets.firstOrNull() ?: return@mapNotNull null
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
                    suggestLoad(
                        history = history,
                        target = target,
                        policy = exercise.progressionPolicy,
                        loadIncrement = exercise.loadIncrement,
                        massUnit = massUnit,
                        isDeloadWeek = isDeloadWeek,
                    )
                        ?: LoadAdvice.OneMoreSession.takeIf {
                            suggestLoad.isOneSessionShort(history, exercise.progressionPolicy)
                        }
                advice?.let { exercise.id to it }
            }
            .toMap()
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
