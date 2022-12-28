package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.functionality.database.routine.ExerciseWithRoutineEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.random.Random

@Suppress("MagicNumber")
class InsertDefaultRoutines @Inject constructor(
    private val routineDao: RoutineDao,
    private val exerciseDao: ExerciseDao,
) {

    suspend operator fun invoke() {

        val random = Random.Default
        val exerciseIDs = exerciseDao
            .getAllExercises()
            .first()
            .map { exerciseEntity -> exerciseEntity.id }

        listOf(
            RoutineEntity(name = "Sample 1"),
            RoutineEntity(name = "Sample 2"),
            RoutineEntity(name = "Sample 3"),
            RoutineEntity(name = "Sample 4"),
            RoutineEntity(name = "Sample 5"),
        ).map { routine ->
            val routineId = routineDao.upsert(routine)
            val freeExerciseIDs = exerciseIDs.toMutableList()
            repeat(random.nextInt(6, 9)) {
                routineDao.insert(
                    ExerciseWithRoutineEntity(
                        routineId = routineId,
                        exerciseId = freeExerciseIDs.removeAt(random.nextInt(freeExerciseIDs.size)),
                    ),
                )
            }
        }
    }
}
