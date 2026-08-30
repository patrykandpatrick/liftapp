package com.patrykandpatrick.liftapp.functionality.database.exercise

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "exercise_id") val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "exercise_name", index = true) val name: Name,
    @ColumnInfo(name = "exercise_type", index = true) val exerciseType: ExerciseType,
    @ColumnInfo(name = "exercise_main_muscles", index = true) val mainMuscles: List<Muscle>,
    @ColumnInfo(name = "exercise_secondary_muscles") val secondaryMuscles: List<Muscle>,
    @ColumnInfo(name = "exercise_tertiary_muscles") val tertiaryMuscles: List<Muscle>,
    /**
     * Nullable with no default, so an exercise nobody has classified stays visibly unclassified
     * rather than claiming a role it was never given. Restoring an archive written before this
     * column existed lands here too.
     */
    @ColumnInfo(name = "exercise_role") val role: ExerciseRole? = null,
    @ColumnInfo(name = "exercise_progression_policy", defaultValue = "DoubleProgression")
    val progressionPolicy: ProgressionPolicy = ProgressionPolicy.DoubleProgression,
    /** The equipment's smallest step, in kilograms. */
    @ColumnInfo(name = "exercise_load_increment") val loadIncrement: Double? = null,
) {

    /**
     * The columns the exercise editor writes. Room updates only what is declared here, so
     * `exercise_load_increment` keeps whatever the row already held.
     */
    class Update(
        @ColumnInfo(name = "exercise_id") val id: Long,
        @ColumnInfo(name = "exercise_name") val name: Name,
        @ColumnInfo(name = "exercise_type") val exerciseType: ExerciseType,
        @ColumnInfo(name = "exercise_main_muscles") val mainMuscles: List<Muscle>,
        @ColumnInfo(name = "exercise_secondary_muscles") val secondaryMuscles: List<Muscle>,
        @ColumnInfo(name = "exercise_tertiary_muscles") val tertiaryMuscles: List<Muscle>,
        @ColumnInfo(name = "exercise_role") val role: ExerciseRole?,
        @ColumnInfo(name = "exercise_progression_policy") val progressionPolicy: ProgressionPolicy,
    )
}
