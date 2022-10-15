package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.functionality.database.routine.ExerciseWithRoutineEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity
import javax.inject.Inject
import kotlin.random.Random

@Suppress("MagicNumber")
class InsertDefaultRoutines @Inject constructor(
    private val routineDao: RoutineDao,
) {

    suspend operator fun invoke() {

        val random = Random.Default

        listOf(
            RoutineEntity(name = "Sample 1"),
            RoutineEntity(name = "Sample 2"),
            RoutineEntity(name = "Sample 3"),
            RoutineEntity(name = "Sample 4"),
            RoutineEntity(name = "Sample 5"),
        ).map { routine ->
            val routineId = routineDao.upsert(routine)
            repeat(random.nextInt(6, 9)) {
                routineDao.insert(
                    ExerciseWithRoutineEntity(
                        routineId = routineId,
                        exerciseId = random.nextLong(104),
                    ),
                )
            }
        }
    }
}
