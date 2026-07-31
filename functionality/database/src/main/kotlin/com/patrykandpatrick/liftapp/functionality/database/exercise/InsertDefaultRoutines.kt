package com.patrykandpatrick.liftapp.functionality.database.exercise

import com.patrykandpatrick.liftapp.functionality.database.routine.ExerciseWithRoutineEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.flow.first

@Suppress("MagicNumber")
class InsertDefaultRoutines
@Inject
constructor(private val routineDao: RoutineDao, private val exerciseDao: ExerciseDao) {

    suspend operator fun invoke() {
        val random = Random.Default
        val exerciseIDs =
            exerciseDao.getAllExercises().first().map { exerciseEntity -> exerciseEntity.id }

        listOf(
                RoutineEntity(name = "Sample 1"),
                RoutineEntity(name = "Sample 2"),
                RoutineEntity(name = "Sample 3"),
                RoutineEntity(name = "Sample 4"),
                RoutineEntity(name = "Sample 5"),
            )
            .map { routine ->
                val routineId = routineDao.upsert(routine)
                val freeExerciseIDs = exerciseIDs.toMutableList()
                repeat(random.nextInt(6, 9)) { index ->
                    routineDao.insert(
                        ExerciseWithRoutineEntity(
                            routineId = routineId,
                            exerciseId =
                                freeExerciseIDs.removeAt(random.nextInt(freeExerciseIDs.size)),
                            orderIndex = index,
                        )
                    )
                }
            }
    }
}
