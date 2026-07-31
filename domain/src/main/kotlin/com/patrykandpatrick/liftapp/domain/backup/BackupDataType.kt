package com.patrykandpatrick.liftapp.domain.backup

import kotlinx.serialization.Serializable

/**
 * A category of data the user can pick when backing up or restoring.
 *
 * The categories are not independent: a workout references the routine it was started from, and a
 * training plan references the routines it schedules. [dependencies] names what a category cannot
 * be read back without, so both the export and the import expand the user's selection with
 * [withDependencies] before touching anything.
 */
@Serializable
enum class BackupDataType {
    Routines,
    Workouts,
    TrainingPlans,
    BodyMeasurements,
    Settings;

    val dependencies: Set<BackupDataType>
        get() =
            when (this) {
                Workouts,
                TrainingPlans -> setOf(Routines)
                Routines,
                BodyMeasurements,
                Settings -> emptySet()
            }
}

/** [this] together with everything it cannot be restored without. */
val BackupDataType.withDependencies: Set<BackupDataType>
    get() = dependencies + this

/** [this] together with everything the contained types cannot be restored without. */
fun Set<BackupDataType>.withDependencies(): Set<BackupDataType> =
    flatMapTo(mutableSetOf()) { it.withDependencies }

/**
 * The members of [this] that another member depends on. A picker offers these as checked and
 * locked: unchecking one would make the rest of the selection unrestorable.
 */
fun Set<BackupDataType>.requiredWithin(): Set<BackupDataType> =
    filterTo(mutableSetOf()) { type ->
        any { other -> other != type && type in other.dependencies }
    }
