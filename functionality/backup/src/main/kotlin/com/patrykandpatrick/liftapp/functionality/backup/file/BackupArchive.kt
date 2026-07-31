package com.patrykandpatrick.liftapp.functionality.backup.file

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/** Whether [onEntry] wants to keep walking the archive. */
internal enum class ArchiveStep {
    Continue,
    Stop,
}

/**
 * Walks a backup ZIP, handing each entry to [onEntry] as a reader.
 *
 * A new reader is made for every entry on purpose. A shared [InputStreamReader] latches
 * end-of-input the first time the entry beneath it runs out, and would then report every later
 * entry as empty.
 */
internal inline fun ZipInputStream.forEachEntry(
    onEntry: (name: String, reader: BufferedReader) -> ArchiveStep
) {
    var entry: ZipEntry? = nextEntry
    while (entry != null) {
        if (!entry.isDirectory) {
            val reader = BufferedReader(InputStreamReader(this, Charsets.UTF_8))
            if (onEntry(entry.name, reader) == ArchiveStep.Stop) return
        }
        entry = nextEntry
    }
}

internal inline fun readArchive(
    stream: InputStream,
    onEntry: (name: String, reader: BufferedReader) -> ArchiveStep,
) {
    ZipInputStream(stream.buffered()).use { it.forEachEntry(onEntry) }
}

/**
 * Writes a backup ZIP. Each [entry] gets its own writer, which is flushed before the entry closes
 * so nothing is left sitting in a buffer when the next one starts.
 */
internal class BackupArchiveWriter(stream: OutputStream) : AutoCloseable {

    private val zip = ZipOutputStream(stream.buffered())

    fun entry(name: String, write: (Writer) -> Unit) {
        zip.putNextEntry(ZipEntry(name))
        val writer = OutputStreamWriter(zip, Charsets.UTF_8)
        write(writer)
        writer.flush()
        zip.closeEntry()
    }

    override fun close() {
        zip.close()
    }
}
