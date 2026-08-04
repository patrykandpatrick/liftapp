package com.patrykandpatrick.liftapp.functionality.backup.file

import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class BackupTableTest {

    @Test
    fun `every data type but settings owns at least one table`() {
        val covered = BackupTable.entries.map { it.type }.toSet()
        assertEquals(BackupDataType.entries.toSet() - BackupDataType.Settings, covered)
    }

    @Test
    fun `no two tables share a name or an entry path`() {
        assertEquals(
            BackupTable.entries.size,
            BackupTable.entries.map { it.tableName }.toSet().size,
        )
        assertEquals(
            BackupTable.entries.size,
            BackupTable.entries.map { it.entryPath }.toSet().size,
        )
    }

    @Test
    fun `a type's tables never straddle another type's`() {
        // The import replays this list top to bottom, so a type has to be contiguous for its own
        // tables to land in one run.
        val order = BackupTable.entries.map { it.type }
        assertEquals(order.distinct().size, order.zipWithNext().count { (a, b) -> a != b } + 1)
    }

    @Test
    fun `a type is written after everything it depends on`() {
        BackupTable.entries.forEachIndexed { index, table ->
            table.type.dependencies.forEach { dependency ->
                val last = BackupTable.entries.indexOfLast { it.type == dependency }
                assertTrue(
                    last < index,
                    "${table.tableName} is written before $dependency, which it references.",
                )
            }
        }
    }

    @Test
    fun `the routine filter binds the routine ID once per placeholder`() {
        BackupTable.entries.forEach { table ->
            val placeholders = table.routineFilter?.count { it == '?' } ?: 0
            assertEquals(placeholders, table.routineArgumentCount, "${table.tableName} disagrees.")
        }
    }

    @Test
    fun `sharing a routine covers every routine table`() {
        val routineTables = BackupTable.entries.filter { it.type == BackupDataType.Routines }
        assertEquals(routineTables, routineTables.filter { it.routineFilter != null })
    }
}
