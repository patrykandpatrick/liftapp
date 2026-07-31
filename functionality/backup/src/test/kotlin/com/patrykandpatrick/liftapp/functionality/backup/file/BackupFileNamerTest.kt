package com.patrykandpatrick.liftapp.functionality.backup.file

import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class BackupFileNamerTest {

    private val namer = BackupFileNamer(TestStringProvider)
    private val date = LocalDate.of(2026, 7, 30)

    @Test
    fun `names a backup holding everything a full backup`() {
        assertEquals(
            "Full backup (30 Jul 2026).lfa",
            namer.name(BackupDataType.entries.toSet(), automatic = false, date = date),
        )
    }

    @Test
    fun `names a scheduled backup after the schedule, not its contents`() {
        assertEquals(
            "Auto backup (30 Jul 2026).auto.lfa",
            namer.name(BackupDataType.entries.toSet(), automatic = true, date = date),
        )
    }

    @Test
    fun `lists what a partial backup holds, in the order the picker shows`() {
        assertEquals(
            "Routine and workout backup (30 Jul 2026).lfa",
            namer.name(
                setOf(BackupDataType.Workouts, BackupDataType.Routines),
                automatic = false,
                date = date,
            ),
        )
    }

    @Test
    fun `punctuates a longer list the way the locale does`() {
        assertEquals(
            "Routine, workout, and body-measurement backup (30 Jul 2026).lfa",
            namer.name(
                setOf(
                    BackupDataType.BodyMeasurements,
                    BackupDataType.Routines,
                    BackupDataType.Workouts,
                ),
                automatic = false,
                date = date,
            ),
        )
    }

    @Test
    fun `names the data types attributively so they modify the word backup`() {
        assertEquals(
            "Body-measurement backup (30 Jul 2026).lfa",
            namer.name(setOf(BackupDataType.BodyMeasurements), automatic = false, date = date),
        )
    }

    @Test
    fun `recognizes the backups it named itself`() {
        val automatic = namer.name(setOf(BackupDataType.Routines), automatic = true, date = date)
        val manual = namer.name(setOf(BackupDataType.Routines), automatic = false, date = date)

        assertTrue(namer.isAutomatic(automatic))
        assertTrue(namer.isAutomatic(automatic.removeSuffix(".lfa") + " 2.lfa"))
        assertFalse(namer.isAutomatic(manual))
    }
}
