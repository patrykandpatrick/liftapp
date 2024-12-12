package com.patrykandpatryk.liftapp.functionality.database.goal

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface GoalDao {
    @Query(
        "SELECT * FROM goal WHERE goal_routine_id = :routineID AND goal_exercise_id = :exerciseID LIMIT 1"
    )
    suspend fun getGoal(routineID: Long, exerciseID: Long): GoalEntity?

    @Query("SELECT exercise_goal FROM exercise WHERE exercise_id = :exerciseID LIMIT 1")
    suspend fun getDefaultGoal(exerciseID: Long): ExerciseGoal?

    @Upsert(entity = GoalEntity::class) suspend fun saveGoal(goal: GoalEntity)
}
