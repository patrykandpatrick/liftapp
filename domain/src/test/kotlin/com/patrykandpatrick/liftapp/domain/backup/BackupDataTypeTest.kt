package com.patrykandpatrick.liftapp.domain.backup

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class BackupDataTypeTest {

    @Test
    fun `a workout drags in the routine it was started from`() {
        assertEquals(
            setOf(BackupDataType.Workouts, BackupDataType.Routines),
            setOf(BackupDataType.Workouts).withDependencies(),
        )
    }

    @Test
    fun `a training plan drags in the routines it schedules`() {
        assertEquals(
            setOf(BackupDataType.TrainingPlans, BackupDataType.Routines),
            setOf(BackupDataType.TrainingPlans).withDependencies(),
        )
    }

    @Test
    fun `types that stand on their own drag in nothing`() {
        listOf(BackupDataType.Routines, BackupDataType.BodyMeasurements, BackupDataType.Settings)
            .forEach { type -> assertEquals(setOf(type), setOf(type).withDependencies()) }
    }

    @Test
    fun `expanding is idempotent`() {
        val once = BackupDataType.entries.toSet().withDependencies()
        assertEquals(once, once.withDependencies())
    }

    @Test
    fun `a routine is required only once something else in the selection needs it`() {
        assertEquals(emptySet(), setOf(BackupDataType.Routines).requiredWithin())
        assertEquals(
            setOf(BackupDataType.Routines),
            setOf(BackupDataType.Routines, BackupDataType.Workouts).requiredWithin(),
        )
    }

    @Test
    fun `nothing depends on itself, directly or otherwise`() {
        BackupDataType.entries.forEach { type ->
            assertTrue(type !in type.dependencies, "$type depends on itself.")
            type.dependencies.forEach { dependency ->
                assertTrue(
                    dependency.dependencies.isEmpty(),
                    "$dependency has dependencies of its own, which expansion does not follow.",
                )
            }
        }
    }
}
