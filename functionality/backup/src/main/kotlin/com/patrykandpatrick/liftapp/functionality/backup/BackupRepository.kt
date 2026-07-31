package com.patrykandpatrick.liftapp.functionality.backup

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupFile
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.ExportBackupUseCase
import com.patrykandpatrick.liftapp.domain.backup.ExportRoutineUseCase
import com.patrykandpatrick.liftapp.domain.backup.GetDirectoryNameUseCase
import com.patrykandpatrick.liftapp.domain.backup.GetShareableLocationUseCase
import com.patrykandpatrick.liftapp.domain.backup.ImportBackupUseCase
import com.patrykandpatrick.liftapp.domain.backup.PersistDirectoryAccessUseCase
import com.patrykandpatrick.liftapp.domain.backup.ReadBackupContentsUseCase
import com.patrykandpatrick.liftapp.domain.backup.withDependencies
import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.exception.DisplayableException
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.functionality.backup.file.ArchiveStep
import com.patrykandpatrick.liftapp.functionality.backup.file.BackupArchiveWriter
import com.patrykandpatrick.liftapp.functionality.backup.file.BackupFileNamer
import com.patrykandpatrick.liftapp.functionality.backup.file.BackupFormat
import com.patrykandpatrick.liftapp.functionality.backup.file.BackupManifest
import com.patrykandpatrick.liftapp.functionality.backup.file.BackupTable
import com.patrykandpatrick.liftapp.functionality.backup.file.LegacyBackup
import com.patrykandpatrick.liftapp.functionality.backup.file.PreferencesCsv
import com.patrykandpatrick.liftapp.functionality.backup.file.SqliteCsv
import com.patrykandpatrick.liftapp.functionality.backup.file.readArchive
import com.patrykandpatrick.liftapp.functionality.backup.storage.DocumentStorage
import com.patrykandpatrick.liftapp.functionality.database.Database
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Writes and reads `.lfa` backup files.
 *
 * Both directions run under [NonCancellable]. A half-written file is worse than no file, and a
 * half-replayed import would leave the database inconsistent in a way the user cannot see.
 */
@Singleton
class BackupRepository
@Inject
constructor(
    private val application: Application,
    private val database: Database,
    private val dataStore: DataStore<Preferences>,
    private val storage: DocumentStorage,
    private val namer: BackupFileNamer,
    private val stringProvider: StringProvider,
    private val json: Json,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) :
    ExportBackupUseCase,
    ExportRoutineUseCase,
    ImportBackupUseCase,
    ReadBackupContentsUseCase,
    GetDirectoryNameUseCase,
    GetShareableLocationUseCase,
    PersistDirectoryAccessUseCase {

    private val appVersionName: String by lazy {
        runCatching {
            application.packageManager.getPackageInfo(application.packageName, 0).versionName
        }
            .getOrNull()
            .orEmpty()
    }

    override suspend fun exportBackup(
        directory: BackupLocation,
        types: Set<BackupDataType>,
        automatic: Boolean,
    ): BackupFile =
        withContext(dispatcher + NonCancellable) {
            require(types.isNotEmpty()) { "Nothing was selected to back up." }
            val contents = types.expanded()
            val location =
                storage.createFile(
                    directory = directory,
                    name = namer.name(contents, automatic, LocalDate.now()),
                    mimeType = BackupFormat.MIME_TYPE,
                )

            try {
                write(location, contents, routineID = null)
            } catch (throwable: Throwable) {
                Timber.e(throwable, "Could not write a backup to ${location.value}.")
                // A half-written file would still look like a usable backup, so take it back out.
                storage.delete(location)
                throw throwable
            }

            checkNotNull(storage.describe(location)) { "The backup vanished after being written." }
        }

    override suspend fun exportRoutine(routineID: Long): BackupFile =
        withContext(dispatcher + NonCancellable) {
            val contents = setOf(BackupDataType.Routines)
            val name = namer.name(contents, automatic = false, date = LocalDate.now())
            val location = storage.createCacheFile(name)
            write(location, contents, routineID)
            checkNotNull(storage.describe(location)) { "The backup vanished after being written." }
        }

    override suspend fun readBackupContents(location: BackupLocation): Set<BackupDataType> =
        withContext(dispatcher) { contentsOf(location) }

    override suspend fun importBackup(location: BackupLocation, types: Set<BackupDataType>) {
        withContext(dispatcher + NonCancellable) {
            val available = contentsOf(location)
            val wanted = types.expanded().intersect(available)
            if (wanted.isEmpty()) return@withContext

            val preferences = restoreTables(location, wanted)
            preferences?.let { apply -> dataStore.edit(apply) }
        }
    }

    override suspend fun getDirectoryName(location: BackupLocation): String? =
        withContext(dispatcher) { storage.directoryName(location) }

    override suspend fun getShareableLocation(location: BackupLocation): BackupLocation =
        withContext(dispatcher) { storage.shareableLocation(location) }

    override fun persistDirectoryAccess(location: BackupLocation) {
        storage.persistAccess(location)
    }

    /** Removes the scheduled backups in [directory] that are older than [maxAgeDays]. */
    suspend fun deleteExpiredBackups(directory: BackupLocation, maxAgeDays: Int) {
        withContext(dispatcher) {
            val cutoff = LocalDateTime.now().minusDays(maxAgeDays.toLong())
            storage
                .listBackups(listOf(directory))
                .filter { namer.isAutomatic(it.name) && it.lastModified.isBefore(cutoff) }
                .forEach { file ->
                    Timber.i("Deleting the expired backup ${file.name}.")
                    storage.delete(file.location)
                }
        }
    }

    private suspend fun write(
        location: BackupLocation,
        contents: Set<BackupDataType>,
        routineID: Long?,
    ) {
        val db = database.openHelper.writableDatabase
        val preferences = if (BackupDataType.Settings in contents) dataStore.data.first() else null

        BackupArchiveWriter(storage.openOutput(location)).use { archive ->
            archive.entry(BackupFormat.MANIFEST_NAME) { writer ->
                writer.write(
                    json.encodeToString(
                        BackupManifest(
                            formatVersion = BackupFormat.VERSION,
                            createdAt = LocalDateTime.now().toString(),
                            appVersionName = appVersionName,
                            contents = BackupDataType.entries.filter { it in contents },
                        )
                    )
                )
            }

            BackupTable.entries
                .filter { it.type in contents }
                .let { tables ->
                    // Keep every table in the archive on one database snapshot. A workout can be
                    // updated while an automatic backup is running, and mixing states from either
                    // side of that write can leave the archive with broken foreign keys.
                    db.beginTransactionNonExclusive()
                    try {
                        tables.forEach { table ->
                            val where = if (routineID == null) null else table.routineFilter
                            if (routineID != null && where == null) return@forEach
                            val arguments =
                                if (routineID == null) emptyArray<Any>()
                                else Array<Any>(table.routineArgumentCount) { routineID }
                            archive.entry(table.entryPath) { writer ->
                                SqliteCsv.write(db, table.tableName, writer, where, arguments)
                            }
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }

            if (preferences != null) {
                archive.entry(PREFERENCES_PATH) { writer ->
                    PreferencesCsv.write(preferences, writer, json)
                }
            }
        }
    }

    /**
     * Replays the tables belonging to [types] in one transaction and returns the preference edit
     * the file asked for, if any. Preferences are applied by the caller: DataStore is not part of
     * the database transaction, and editing it here would suspend inside one.
     */
    private fun restoreTables(
        location: BackupLocation,
        types: Set<BackupDataType>,
    ): ((MutablePreferences) -> Unit)? {
        val legacy = if (readManifest(location) == null) readLegacyBackup(location) else null
        val tables = BackupTable.entries.filter { it.type in types }
        val db = database.openHelper.writableDatabase
        var preferences: ((MutablePreferences) -> Unit)? = null
        var restored = false

        // Foreign keys go off for the replay, and the pragma only takes outside a transaction.
        //
        // Replacing a row that something references would otherwise cascade the delete: restoring a
        // routines-only backup would replace each routine row, and every workout started from those
        // routines would go with it. The rows being written are a consistent snapshot, so the
        // constraints are checked once at the end instead — before the transaction commits.
        db.setForeignKeyConstraintsEnabled(false)
        try {
            db.beginTransaction()
            try {
                if (legacy != null) {
                    restored = legacy.restore(db, types, json)
                    if (BackupDataType.Settings in types) {
                        preferences = legacy.preferenceEdit(json)
                        restored = restored || preferences != null
                    }
                } else {
                    readArchive(storage.openInput(location)) { name, reader ->
                        val table = tables.firstOrNull { it.entryPath == name }
                        when {
                            table != null -> {
                                SqliteCsv.read(db, table.tableName, reader)
                                restored = true
                            }

                            BackupDataType.Settings in types && name == PREFERENCES_PATH -> {
                                preferences = PreferencesCsv.read(reader, json)
                                restored = true
                            }
                        }
                        ArchiveStep.Continue
                    }
                }

                if (!restored) throw unsupportedFile()
                checkForeignKeys(db)
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } finally {
            db.setForeignKeyConstraintsEnabled(true)
        }
        database.invalidationTracker.refreshAsync()
        return preferences
    }

    /** Refuses a backup that would leave a row pointing at something that is not there. */
    private fun checkForeignKeys(db: SupportSQLiteDatabase) {
        db.query("PRAGMA foreign_key_check").use { cursor ->
            if (cursor.moveToFirst()) {
                Timber.e("The backup leaves ${cursor.count} row(s) with a broken reference.")
                throw DisplayableException.Text(stringProvider.errorBackupFileUnreadable)
            }
        }
    }

    /**
     * What [location] holds, refusing anything this build cannot read in full. A file written by a
     * later version may carry tables that are not replayed here, and a partial restore of one would
     * be worse than none.
     */
    private fun contentsOf(location: BackupLocation): Set<BackupDataType> {
        val manifest = readManifest(location)
        if (manifest == null) {
            return readLegacyBackup(location)?.contents ?: throw unsupportedFile()
        }
        if (manifest.formatVersion > BackupFormat.VERSION) {
            Timber.w(
                "Backup format ${manifest.formatVersion} is newer than ${BackupFormat.VERSION}."
            )
            throw unsupportedFile()
        }
        return manifest.contents.toSet()
    }

    private fun readLegacyBackup(location: BackupLocation): LegacyBackup? =
        LegacyBackup.read(storage.openInput(location))

    private fun readManifest(location: BackupLocation): BackupManifest? {
        var manifest: BackupManifest? = null
        runCatching {
            readArchive(storage.openInput(location)) { name, reader ->
                if (name != BackupFormat.MANIFEST_NAME) {
                    ArchiveStep.Continue
                } else {
                    manifest = json.decodeFromString(reader.readText())
                    ArchiveStep.Stop
                }
            }
        }
            .onFailure { Timber.w(it, "Could not read the manifest of ${location.value}.") }
        return manifest
    }

    private fun unsupportedFile() =
        DisplayableException.Text(stringProvider.errorBackupFileUnsupported)

    private fun Set<BackupDataType>.expanded(): Set<BackupDataType> =
        flatMapTo(mutableSetOf()) { it.withDependencies }

    private companion object {
        val PREFERENCES_PATH =
            BackupFormat.entryPath(BackupDataType.Settings, BackupFormat.PREFERENCES_NAME)
    }
}
