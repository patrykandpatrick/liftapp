package com.patrykandpatrick.liftapp.functionality.backup.file

import java.io.StringReader
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test

class CsvTest {

    @Test
    fun `writes every non-null field quoted`() {
        assertEquals("\"a\",\"b\"\n", write(listOf(listOf("a", "b"))))
    }

    @Test
    fun `writes null as an empty field and an empty string as an empty quoted field`() {
        assertEquals("\"\",\n", write(listOf(listOf("", null))))
    }

    @Test
    fun `escapes quotes by doubling them`() {
        assertEquals("\"say \"\"hi\"\"\"\n", write(listOf(listOf("say \"hi\""))))
    }

    @Test
    fun `tells an empty string apart from a null on the way back`() {
        assertEquals(listOf(listOf("", null)), read("\"\",\n"))
    }

    @Test
    fun `reads values holding separators and newlines`() {
        assertEquals(listOf(listOf("a,b\nc")), read("\"a,b\nc\"\n"))
    }

    @Test
    fun `reads a final row that has no trailing newline`() {
        assertEquals(listOf(listOf("a"), listOf("b")), read("\"a\"\n\"b\""))
    }

    @Test
    fun `round-trips the values a backup actually carries`() {
        val rows =
            listOf(
                listOf("id", "name", "notes", "weight"),
                listOf("1", "Bench press", "", null),
                listOf("2", "Quoted \"name\"", "line\nbreak", "62.5"),
                listOf("3", "Comma, separated", " ", "-0.0"),
            )
        assertEquals(rows, read(write(rows)))
    }

    @Test
    fun `rejects a file that ends inside a value`() {
        assertFailsWith<CsvFormatException> { read("\"unterminated") }
    }

    @Test
    fun `rejects unquoted text`() {
        assertFailsWith<CsvFormatException> { read("bare\n") }
    }

    private fun write(rows: List<List<String?>>): String =
        StringWriter().also { Csv.write(it, rows.asSequence()) }.toString()

    private fun read(text: String): List<List<String?>> = Csv.read(StringReader(text)).toList()
}
