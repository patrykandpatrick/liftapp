package com.patrykandpatrick.liftapp.functionality.backup.file

import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.Reader
import java.io.Writer
import timber.log.Timber

/**
 * Reads and writes a single table as CSV.
 *
 * Nothing here names a column. The header row is whatever the table declares, which is what lets
 * one pair of functions cover all sixteen tables and keeps the file honest when the schema changes.
 */
internal object SqliteCsv {

    fun write(
        database: SupportSQLiteDatabase,
        table: String,
        writer: Writer,
        where: String? = null,
        arguments: Array<Any> = emptyArray(),
    ) {
        val sql = "SELECT * FROM `$table`" + if (where != null) " WHERE $where" else ""
        database.query(sql, arguments).use { cursor ->
            Csv.write(
                writer,
                sequence {
                    yield(cursor.columnNames.toList())
                    while (cursor.moveToNext()) {
                        yield(List(cursor.columnCount) { index -> cursor.value(index) })
                    }
                },
            )
        }
    }

    /**
     * Replays [reader] into [table]. Rows that collide with an existing primary key or unique index
     * replace it, which is what makes restoring the same backup twice a no-op.
     */
    fun read(database: SupportSQLiteDatabase, table: String, reader: Reader) {
        val rows = Csv.read(reader).iterator()
        if (!rows.hasNext()) return

        val header =
            rows.next().mapIndexed { index, name ->
                name ?: throw CsvFormatException("Column $index of `$table` has no name.")
            }
        val known = database.columnNames(table)
        val indices = header.indices.filter { header[it] in known }

        (header.indices - indices.toSet()).forEach { index ->
            Timber.w("Ignoring unknown column `${header[index]}` of `$table`.")
        }
        if (indices.isEmpty()) return

        val columns = indices.joinToString { "`${header[it]}`" }
        val placeholders = indices.joinToString { "?" }

        database
            .compileStatement("INSERT OR REPLACE INTO `$table` ($columns) VALUES ($placeholders)")
            .use { statement ->
                rows.forEach { row ->
                    statement.clearBindings()
                    indices.forEachIndexed { position, index ->
                        // Everything is bound as text on purpose. SQLite applies the column's
                        // affinity to a bound value before storing it, so an INTEGER column gets an
                        // integer and a REAL column gets a float without this having to know which
                        // is which.
                        when (val value = row.getOrNull(index)) {
                            null -> statement.bindNull(position + 1)
                            else -> statement.bindString(position + 1, value)
                        }
                    }
                    statement.executeInsert()
                }
            }
    }

    private fun Cursor.value(index: Int): String? =
        when (getType(index)) {
            Cursor.FIELD_TYPE_NULL -> null
            // Cursor.getString would round a double to fifteen significant digits. Double.toString
            // gives back the shortest text that parses to the same value.
            Cursor.FIELD_TYPE_FLOAT -> getDouble(index).toString()
            else -> getString(index)
        }

    private fun SupportSQLiteDatabase.columnNames(table: String): Set<String> =
        query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            buildSet { while (cursor.moveToNext()) add(cursor.getString(nameIndex)) }
        }
}
