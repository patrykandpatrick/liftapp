package com.patrykandpatrick.liftapp.functionality.database.exercise

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.functionality.database.workout.ExerciseSetEntity
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

class ExerciseSetMapperTest {

    private val preferences = TestPreferenceRepository()

    private val mapper =
        ExerciseSetMapper(
            converter =
                UnitConverter(
                    formatter = Formatter(TestStringProvider, preferences),
                    stringProvider = TestStringProvider,
                    preferences = preferences,
                ),
            preferenceRepository = preferences,
        )

    private fun entity(rir: Int?) =
        ExerciseSetEntity(
            id = 1,
            workoutID = 1,
            exerciseID = 1,
            weight = 100.0,
            weightUnit = MassUnit.Kilograms,
            reps = 8,
            rir = rir,
            setIndex = 0,
        )

    private fun map(rir: Int?, type: ExerciseType = ExerciseType.Weight) =
        mapper
            .mapWorkoutExerciseSets(
                exerciseType = type,
                setCount = 1,
                sets = mapOf(0 to entity(rir)),
                massUnit = MassUnit.Kilograms,
                distanceUnit = LongDistanceUnit.Kilometer,
            )
            .single()

    @Test
    fun `a reported rir survives the mapping`() {
        assertEquals(2, map(rir = 2).rir)
        assertEquals(2, map(rir = 2, type = ExerciseType.Calisthenics).rir)
        assertEquals(2, map(rir = 2, type = ExerciseType.Reps).rir)
    }

    @Test
    fun `zero rir is kept rather than treated as absent`() {
        // Every other nullable column falls back to zero when absent, so zero has to survive here
        // to stay distinguishable from "not reported": zero means the set went to failure.
        assertEquals(0, map(rir = 0).rir)
    }

    @Test
    fun `an unreported rir stays null`() {
        assertNull(map(rir = null).rir)
        assertNull(map(rir = null, type = ExerciseType.Reps).rir)
    }

    @Test
    fun `a set type without a meaningful rir never reports one`() {
        assertNull(map(rir = 3, type = ExerciseType.Cardio).rir)
        assertNull(map(rir = 3, type = ExerciseType.Time).rir)
    }

    @Test
    fun `a rir from outside the reportable range is clamped rather than surfaced`() {
        // Nothing in the app writes one, but a restored archive replays whatever the file holds.
        assertEquals(ExerciseSet.RIR_RANGE.last, map(rir = 9).rir)
        assertEquals(ExerciseSet.RIR_RANGE.first, map(rir = -2).rir)
        assertEquals(ExerciseSet.RIR_RANGE.last, map(rir = 9, type = ExerciseType.Reps).rir)
    }
}
