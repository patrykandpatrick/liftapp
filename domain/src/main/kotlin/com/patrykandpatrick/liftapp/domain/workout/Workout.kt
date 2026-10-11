package com.patrykandpatrick.liftapp.domain.workout

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class Workout(
    val id: Long,
    val routineID: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val notes: String,
    val exercises: List<Exercise>,
) : Serializable {
    val isCompleted: Boolean
        get() = endDate != null

    data class Exercise(
        val id: Long,
        val name: Name,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
        val goal: Goal,
        val notes: String = "",
        val sets: List<ExerciseSet>,
        val lastSets: List<ExerciseSet> = emptyList(),
        val progressionPolicy: ProgressionPolicy = ProgressionPolicy.DoubleProgression,
        /** The equipment's smallest step, in kilograms. */
        val loadIncrement: Double? = null,
        val workoutItemID: Long = id,
        val workoutItemType: RoutineItemType = RoutineItemType.Exercise,
        val workoutItemOrder: Int = 0,
        val exerciseOrder: Int = 0,
    ) : Serializable {
        val completedSets = sets.count { it.isCompleted }
        val totalSets = sets.size

        val isSuperset: Boolean
            get() = workoutItemType == RoutineItemType.Superset
    }

    /**
     * What this workout was prescribed, snapshotted when it started.
     *
     * A copy rather than a reference to the plan's target: re-authoring a plan must not rewrite
     * what a session that already happened was asked to do.
     *
     * [setTargets] is the prescription; everything else here is read off it, so a set count and a
     * list of sets can never disagree.
     */
    data class Goal(
        val id: Long,
        val setTargets: List<SetTarget>,
        val restTime: Duration,
        val distanceUnit: LongDistanceUnit = LongDistanceUnit.Kilometer,
    ) {
        val sets: Int
            get() = setTargets.size

        val minReps: Int
            get() = setTargets.minOfOrNull { it.minReps } ?: 0

        val maxReps: Int
            get() = setTargets.maxOfOrNull { it.maxReps } ?: 0

        val duration: Duration
            get() = setTargets.firstOrNull()?.duration ?: Duration.ZERO

        val distance: Double
            get() = setTargets.firstOrNull()?.distance ?: 0.0

        val calories: Double
            get() = setTargets.firstOrNull()?.calories ?: 0.0

        /** Null past the end: a set added during the session was never prescribed. */
        fun targetFor(setIndex: Int): SetTarget? = setTargets.getOrNull(setIndex)

        /**
         * Extra sets repeat the last one prescribed rather than arriving blank, which is what the ±
         * buttons in a workout mean by "one more set of this".
         */
        fun withSetCount(count: Int): Goal {
            if (count == setTargets.size) return this
            val last = setTargets.lastOrNull() ?: return this
            return copy(setTargets = List(count) { index -> setTargets.getOrElse(index) { last } })
        }

        companion object {
            val default =
                Goal(
                    id = 0,
                    setTargets = List(3) { SetTarget(minReps = 8, maxReps = 12, rir = 2) },
                    restTime = 2L.minutes,
                )
        }
    }
}
