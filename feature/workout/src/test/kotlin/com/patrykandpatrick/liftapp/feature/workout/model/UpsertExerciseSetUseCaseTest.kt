package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.core.text.DoubleTextFieldState
import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.core.text.StringTextFieldState
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Guards the one thing the reps-in-reserve input cannot express through
 * [com.patrykandpatrick.liftapp.core.text.TextFieldState.value], which falls back to `0` for empty
 * text: an unanswered field has to reach the database as `null`, not as a set taken to failure.
 */
class UpsertExerciseSetUseCaseTest {

    private val formatter = Formatter(TestStringProvider, TestPreferenceRepository())

    private var savedSet: ExerciseSet? = null

    private val useCase = UpsertExerciseSetUseCase { _, _, set, _ -> savedSet = set }

    private fun weightSet(rir: String) =
        EditableExerciseSet.Weight(
            weight = 100.0,
            reps = 8,
            weightInput = DoubleTextFieldState(formatter, initialValue = "100"),
            repsInput = IntTextFieldState(initialValue = "8"),
            weightUnit = MassUnit.Kilograms,
            notesInput = StringTextFieldState(),
            rirInput = IntTextFieldState(initialValue = rir),
        )

    private fun calisthenicsSet(rir: String) =
        EditableExerciseSet.Calisthenics(
            weight = 0.0,
            bodyWeight = 80.0,
            reps = 8,
            formattedBodyWeight = "80 kg",
            weightInput = DoubleTextFieldState(formatter),
            repsInput = IntTextFieldState(initialValue = "8"),
            weightUnit = MassUnit.Kilograms,
            notesInput = StringTextFieldState(),
            rirInput = IntTextFieldState(initialValue = rir),
        )

    private fun repsSet(rir: String) =
        EditableExerciseSet.Reps(
            reps = 8,
            repsInput = IntTextFieldState(initialValue = "8"),
            notesInput = StringTextFieldState(),
            rirInput = IntTextFieldState(initialValue = rir),
        )

    private fun upsert(set: EditableExerciseSet<ExerciseSet>): ExerciseSet {
        runTest { useCase(workoutID = 1, exerciseID = 2, set = set, setIndex = 0) }
        return requireNotNull(savedSet)
    }

    @Test
    fun `an unanswered rir is saved as absent, not as zero`() {
        assertNull(upsert(weightSet(rir = "")).rir)
        assertNull(upsert(calisthenicsSet(rir = "")).rir)
        assertNull(upsert(repsSet(rir = "")).rir)
    }

    @Test
    fun `a rir of zero is saved`() {
        assertEquals(0, upsert(weightSet(rir = "0")).rir)
        assertEquals(0, upsert(calisthenicsSet(rir = "0")).rir)
        assertEquals(0, upsert(repsSet(rir = "0")).rir)
    }

    @Test
    fun `a reported rir is saved`() {
        assertEquals(3, upsert(weightSet(rir = "3")).rir)
        assertEquals(3, upsert(calisthenicsSet(rir = "3")).rir)
        assertEquals(3, upsert(repsSet(rir = "3")).rir)
    }

    @Test
    fun `the rest of the set still reaches the contract`() {
        val set = upsert(weightSet(rir = "2")) as ExerciseSet.Weight

        assertEquals(100.0, set.weight)
        assertEquals(8, set.reps)
        assertEquals(MassUnit.Kilograms, set.weightUnit)
    }
}
