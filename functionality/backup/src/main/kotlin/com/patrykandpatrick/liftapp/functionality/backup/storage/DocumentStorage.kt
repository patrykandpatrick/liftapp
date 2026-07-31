package com.patrykandpatrick.liftapp.functionality.backup.storage

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.patrykandpatrick.liftapp.domain.backup.BackupFile
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.functionality.backup.file.BackupFormat
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Everything the backup code needs from the Storage Access Framework, in one place, so the exporter
 * and importer deal in streams rather than in URIs.
 */
@Singleton
class DocumentStorage @Inject constructor(private val application: Application) {

    private val contentResolver
        get() = application.contentResolver

    /**
     * Holds on to the access the user just granted, so a scheduled backup can still write to the
     * folder days later.
     */
    fun persistAccess(location: BackupLocation) {
        val uri = location.toUri()
        if (uri.scheme != "content") return
        runCatching {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        }
            .onFailure { Timber.w(it, "Could not persist access to $uri.") }
    }

    fun hasAccess(location: BackupLocation): Boolean {
        val uri = location.toUri()
        if (uri.scheme != "content") return document(location) != null
        return contentResolver.persistedUriPermissions.any { it.uri == uri && it.isReadPermission }
    }

    fun openInput(location: BackupLocation): InputStream =
        requireNotNull(contentResolver.openInputStream(location.toUri())) {
            "Nothing to read at ${location.value}."
        }

    fun openOutput(location: BackupLocation): OutputStream =
        requireNotNull(contentResolver.openOutputStream(location.toUri(), "wt")) {
            "Nothing to write to at ${location.value}."
        }

    /**
     * Creates a file named [name] in [directory], stepping the name aside if it is taken so an
     * export never silently overwrites an earlier one.
     */
    fun createFile(directory: BackupLocation, name: String, mimeType: String): BackupLocation {
        val parent =
            requireNotNull(directoryDocument(directory)) { "${directory.value} is not a folder." }
        val taken = parent.listFiles().mapNotNullTo(mutableSetOf()) { it.name }
        val created =
            requireNotNull(parent.createFile(mimeType, nonClashingName(name, taken))) {
                "Could not create a file in ${directory.value}."
            }
        return BackupLocation(created.uri.toString())
    }

    /**
     * Creates a file in the cache directory the manifest shares through a [FileProvider]. The
     * directory is cleared first: it only ever holds the one file the user is sharing right now.
     */
    fun createCacheFile(name: String): BackupLocation {
        val directory = File(application.cacheDir, SHARED_DIRECTORY)
        directory.deleteRecursively()
        directory.mkdirs()
        return BackupLocation(Uri.fromFile(File(directory, name)).toString())
    }

    /**
     * The URI another app may read [location] through. Files in the cache are only reachable
     * through the provider; anything the user picked themselves is already a content URI.
     */
    fun shareableLocation(location: BackupLocation): BackupLocation {
        val uri = location.toUri()
        val path = uri.path
        if (uri.scheme != "file" || path == null) return location
        val shared =
            FileProvider.getUriForFile(
                application,
                "${application.packageName}.provider",
                File(path),
            )
        return BackupLocation(shared.toString())
    }

    fun describe(location: BackupLocation): BackupFile? =
        document(location)?.takeIf { it.exists() }?.toBackupFile()

    fun delete(location: BackupLocation): Boolean = document(location)?.delete() == true

    fun directoryName(location: BackupLocation): String? = directoryDocument(location)?.name

    /** The backup files in [directories], newest first. Directories gone missing are skipped. */
    fun listBackups(directories: List<BackupLocation>): List<BackupFile> =
        directories
            .mapNotNull { directoryDocument(it) }
            .flatMap { directory -> directory.listFiles().asIterable() }
            .filter { it.isFile && it.name?.endsWith(BackupFormat.EXTENSION) == true }
            .map { it.toBackupFile() }
            .sortedByDescending { it.lastModified }

    private fun DocumentFile.toBackupFile() =
        BackupFile(
            location = BackupLocation(uri.toString()),
            name = name.orEmpty(),
            sizeBytes = length(),
            lastModified =
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(lastModified()),
                    ZoneId.systemDefault(),
                ),
        )

    private fun document(location: BackupLocation): DocumentFile? {
        val uri = location.toUri()
        return when {
            uri.scheme == "file" -> uri.path?.let { DocumentFile.fromFile(File(it)) }
            DocumentsContract.isTreeUri(uri) -> DocumentFile.fromTreeUri(application, uri)
            else -> DocumentFile.fromSingleUri(application, uri)
        }
    }

    private fun directoryDocument(location: BackupLocation): DocumentFile? =
        document(location)?.takeIf { it.isDirectory }

    private fun BackupLocation.toUri(): Uri = value.toUri()

    private companion object {
        const val SHARED_DIRECTORY = "shared_backups"

        /** "Full backup (30 Jul 2026).lfa" becomes "Full backup (30 Jul 2026) 2.lfa". */
        fun nonClashingName(name: String, taken: Set<String>): String {
            if (name !in taken) return name
            val base = name.removeSuffix(BackupFormat.EXTENSION)
            return generateSequence(2) { it + 1 }
                .map { index -> "$base $index${BackupFormat.EXTENSION}" }
                .first { it !in taken }
        }
    }
}
