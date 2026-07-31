package com.patrykandpatrick.liftapp.functionality.database.workout

import java.time.LocalDateTime
import kotlin.test.assertContains
import kotlin.test.assertFalse
import org.junit.jupiter.api.Test

class WorkoutDaoQueryTest {

    @Test
    fun `unfinished workouts query contains valid null predicate`() {
        val query = WorkoutDao.getWorkoutsSql(hasEndDate = false)

        assertContains(query, "workout_end_date IS NULL")
        assertFalse("%" in query)
    }

    @Test
    fun `finished workouts query contains valid not-null predicate`() {
        val query = WorkoutDao.getWorkoutsSql(hasEndDate = true)

        assertContains(query, "workout_end_date IS NOT NULL")
        assertFalse("%" in query)
    }

    @Test
    fun `past workout range query filters before joining sets`() {
        val query = WorkoutDao.getPastWorkoutsQuery(LocalDateTime.MIN, LocalDateTime.MAX).sql

        assertContains(query, "w.workout_start_date >= ?")
        assertContains(query, "w.workout_start_date < ?")
    }
}
