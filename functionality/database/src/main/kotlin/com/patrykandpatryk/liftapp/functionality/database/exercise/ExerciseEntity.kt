package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle

@Entity(tableName = "exercise")
class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_id")
    val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "exercise_name", index = true)
    val name: Name,
    @ColumnInfo(name = "exercise_type", index = true)
    val exerciseType: ExerciseType,
    @ColumnInfo(name = "exercise_main_muscles", index = true)
    val mainMuscles: List<Muscle>,
    @ColumnInfo(name = "exercise_secondary_muscles")
    val secondaryMuscles: List<Muscle>,
    @ColumnInfo(name = "exercise_tertiary_muscles")
    val tertiaryMuscles: List<Muscle>,
    @ColumnInfo(name = "exercise_goal")
    val goal: Goal = Goal.Default, // TODO add custom goals for built-in exercises.
) {

    class Update(
        @ColumnInfo(name = "exercise_id")
        val id: Long,
        @ColumnInfo(name = "exercise_name")
        val name: Name,
        @ColumnInfo(name = "exercise_main_muscles")
        val mainMuscles: List<Muscle>,
        @ColumnInfo(name = "exercise_secondary_muscles")
        val secondaryMuscles: List<Muscle>,
        @ColumnInfo(name = "exercise_tertiary_muscles")
        val tertiaryMuscles: List<Muscle>,
    )
}
