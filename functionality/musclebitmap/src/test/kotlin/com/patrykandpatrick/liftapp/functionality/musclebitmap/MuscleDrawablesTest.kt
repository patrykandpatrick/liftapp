package com.patrykandpatrick.liftapp.functionality.musclebitmap

import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.functionality.musclebitmap.provider.frontDrawableRes
import com.patrykandpatrick.liftapp.functionality.musclebitmap.provider.rearDrawableRes
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

/**
 * The drawable accessors `error(...)` for a muscle drawn on the wrong side, so a muscle the
 * generator asks for and the provider has no entry for is a crash rather than a blank diagram.
 */
class MuscleDrawablesTest {

    @Test
    fun `Every muscle the generator draws has art on the side it draws it`() {
        Muscle.entries.forEach { muscle ->
            if (muscle.isFrontMuscle) assertNotEquals(0, muscle.frontDrawableRes, muscle.name)
            if (muscle.isRearMuscle) assertNotEquals(0, muscle.rearDrawableRes, muscle.name)
        }
    }

    @Test
    fun `Every muscle is drawn on at least one side`() {
        Muscle.entries.forEach { muscle ->
            assertTrue(muscle.isFrontMuscle || muscle.isRearMuscle, muscle.name)
        }
    }

    // Each head has its own art on the view it shows from, so the diagram never lights up the rear
    // deltoid for a lateral raise — the lump the split exists to remove. The side head has art for
    // both views, because its lateral fibres wrap round the shoulder.
    @Test
    fun `The deltoid heads are drawn where they are visible`() {
        assertTrue(Muscle.FrontDelts.isFrontMuscle && !Muscle.FrontDelts.isRearMuscle)
        assertTrue(Muscle.RearDelts.isRearMuscle && !Muscle.RearDelts.isFrontMuscle)
        assertTrue(Muscle.SideDelts.isFrontMuscle && Muscle.SideDelts.isRearMuscle)
    }

    @Test
    fun `A muscle drawn on both views has different art for each`() {
        val bothSides = listOf(Muscle.Adductors, Muscle.Forearms, Muscle.SideDelts, Muscle.Traps)

        bothSides.forEach { muscle ->
            assertTrue(muscle.isFrontMuscle && muscle.isRearMuscle, muscle.name)
            assertNotEquals(muscle.frontDrawableRes, muscle.rearDrawableRes, muscle.name)
        }
    }

    @Test
    fun `The muscles that only show from the front are drawn there`() {
        listOf(Muscle.Obliques, Muscle.Serratus).forEach { muscle ->
            assertTrue(muscle.isFrontMuscle && !muscle.isRearMuscle, muscle.name)
        }
    }
}
