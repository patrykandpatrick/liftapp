package com.patrykandpatrick.liftapp.functionality.backup.file

import java.io.Reader
import java.io.Writer

/**
 * RFC 4180 CSV, with one addition: a quoted field is a value and a bare field is `NULL`.
 *
 * The published app used a CSV library that could not tell the two apart, so every empty column
 * came back as an empty string. That is wrong for this schema — `exercise_set_weight` and
 * `workout_end_date` are nullable, and an empty string in either is not the same as no value. Since
 * every non-null field is written quoted, the distinction costs nothing to encode.
 */
internal object Csv {

    private const val QUOTE = '"'
    private const val SEPARATOR = ','

    fun write(writer: Writer, rows: Sequence<List<String?>>) {
        rows.forEach { row ->
            row.forEachIndexed { index, field ->
                if (index > 0) writer.write(SEPARATOR.code)
                if (field != null) {
                    writer.write(QUOTE.code)
                    field.forEach { char ->
                        if (char == QUOTE) writer.write(QUOTE.code)
                        writer.write(char.code)
                    }
                    writer.write(QUOTE.code)
                }
            }
            writer.write("\n")
        }
        writer.flush()
    }

    /**
     * Reads [reader] one row at a time. The rows are produced lazily, so the caller has to consume
     * the sequence before closing [reader].
     */
    fun read(reader: Reader): Sequence<List<String?>> = sequence {
        val field = StringBuilder()
        var row = mutableListOf<String?>()
        var quoted = false
        var fieldStarted = false
        var pendingQuote = false

        fun endField() {
            row.add(if (fieldStarted) field.toString() else null)
            field.setLength(0)
            fieldStarted = false
        }

        while (true) {
            val read = reader.read()
            val char = if (read == -1) null else read.toChar()

            if (pendingQuote) {
                pendingQuote = false
                if (char == QUOTE) {
                    // An escaped quote inside a quoted field.
                    field.append(QUOTE)
                    continue
                }
                quoted = false
            }

            if (char == null) {
                if (quoted) throw CsvFormatException("The file ends inside a quoted value.")
                if (fieldStarted || row.isNotEmpty()) {
                    endField()
                    yield(row)
                }
                return@sequence
            }

            when {
                quoted -> if (char == QUOTE) pendingQuote = true else field.append(char)

                char == QUOTE -> {
                    quoted = true
                    fieldStarted = true
                }

                char == SEPARATOR -> endField()

                char == '\n' -> {
                    endField()
                    yield(row)
                    row = mutableListOf()
                }

                char == '\r' -> Unit

                char.isWhitespace() -> Unit

                else -> throw CsvFormatException("Unquoted text outside a value: “$char”.")
            }
        }
    }
}

internal class CsvFormatException(message: String) : Exception(message)
