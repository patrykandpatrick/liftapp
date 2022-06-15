package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = ID_NOT_SET,
    @ColumnInfo(index = true)
    val name: Name,
    @ColumnInfo(name = "exercise_type", index = true)
    val exerciseType: ExerciseType,
    @ColumnInfo(name = "main_muscles", index = true)
    val mainMuscles: List<Muscle>,
    @ColumnInfo(name = "secondary_muscles")
    val secondaryMuscles: List<Muscle>,
    @ColumnInfo(name = "tertiary_muscles")
    val tertiaryMuscles: List<Muscle>,
) {

    class Update(
        val id: Long,
        val name: Name,
        @ColumnInfo(name = "main_muscles")
        val mainMuscles: List<Muscle>,
        @ColumnInfo(name = "secondary_muscles")
        val secondaryMuscles: List<Muscle>,
        @ColumnInfo(name = "tertiary_muscles")
        val tertiaryMuscles: List<Muscle>,
    )
}
