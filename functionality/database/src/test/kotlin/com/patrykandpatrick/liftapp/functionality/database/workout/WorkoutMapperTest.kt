package com.patrykandpatrick.liftapp.functionality.database.workout

import java.time.LocalDateTime
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

class WorkoutMapperTest {

    @Test
    fun `a workout row without joined items has no exercise`() {
        val row =
            WorkoutWithWorkoutExerciseDto(
                workout =
                    WorkoutEntity(
                        id = 1,
                        routineID = 2,
                        name = "Empty routine",
                        startDate = LocalDateTime.now(),
                        endDate = null,
                        notes = "",
                        bodyWeight = null,
                    ),
                item = null,
                exercise = null,
                goal = null,
                exerciseOrder = null,
                notes = null,
                currentExerciseSet = null,
                lastExerciseSet = null,
            )

        assertNull(row.toWorkoutExerciseDtoOrNull())
    }
}
