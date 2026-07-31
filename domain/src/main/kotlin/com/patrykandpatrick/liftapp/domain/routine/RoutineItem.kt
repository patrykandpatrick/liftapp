package com.patrykandpatrick.liftapp.domain.routine

import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class RoutineItemType {
    Exercise,
    Superset,
}

data class SupersetConfig(val sets: Int = 3, val restTime: Duration = 2.minutes) :
    java.io.Serializable {
    init {
        require(sets > 0) { "A superset must contain at least one set." }
        require(!restTime.isNegative()) { "A superset rest time cannot be negative." }
    }
}

data class RoutineItem(
    val exerciseIDs: List<Long>,
    val type: RoutineItemType,
    val id: Long = ID_NOT_SET,
    val supersetConfig: SupersetConfig? = null,
) : java.io.Serializable {
    init {
        require(exerciseIDs.distinct().size == exerciseIDs.size) {
            "A routine item cannot contain the same exercise more than once."
        }
        when (type) {
            RoutineItemType.Exercise -> {
                require(exerciseIDs.size == 1) { "An exercise item must contain one exercise." }
                require(supersetConfig == null) {
                    "An exercise item cannot have superset settings."
                }
            }
            RoutineItemType.Superset -> {
                require(exerciseIDs.size in MIN_SUPERSET_SIZE..MAX_SUPERSET_SIZE) {
                    "A superset must contain $MIN_SUPERSET_SIZE–$MAX_SUPERSET_SIZE exercises."
                }
                requireNotNull(supersetConfig) { "A superset must have superset settings." }
            }
        }
    }

    companion object {
        const val MIN_SUPERSET_SIZE = 2
        const val MAX_SUPERSET_SIZE = 8

        fun exercise(exerciseID: Long, id: Long = ID_NOT_SET) =
            RoutineItem(exerciseIDs = listOf(exerciseID), type = RoutineItemType.Exercise, id = id)

        fun superset(
            exerciseIDs: List<Long>,
            config: SupersetConfig = SupersetConfig(),
            id: Long = ID_NOT_SET,
        ) =
            RoutineItem(
                exerciseIDs = exerciseIDs,
                type = RoutineItemType.Superset,
                id = id,
                supersetConfig = config,
            )
    }
}

data class RoutineItemWithExercises(
    val id: Long,
    val type: RoutineItemType,
    val exercises: List<RoutineExerciseItem>,
    val supersetConfig: SupersetConfig? = null,
) {
    val isSuperset: Boolean
        get() = type == RoutineItemType.Superset
}
