package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

class RoutineWithExercisesRelation(
    @Embedded
    val routine: RoutineEntity,
    @Relation(
        parentColumn = "id",
        entity = ExerciseEntity::class,
        entityColumn = "id",
        associateBy = Junction(
            value = ExerciseWithRoutineEntity::class,
            parentColumn = "routine_id",
            entityColumn = "exercise_id",
        ),
    )
    val exercises: List<ExerciseEntity>,
)
