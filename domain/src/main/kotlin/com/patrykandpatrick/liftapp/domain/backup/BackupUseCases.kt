package com.patrykandpatrick.liftapp.domain.backup

/**
 * The methods are named rather than `invoke`, because one repository implements all of them and
 * several take the same argument.
 */

/**
 * Writes the selected data into a backup file inside [directory] and returns the file it wrote. The
 * name is generated from what was written and the current date.
 */
fun interface ExportBackupUseCase {
    suspend fun exportBackup(
        directory: BackupLocation,
        types: Set<BackupDataType>,
        automatic: Boolean,
    ): BackupFile
}

/**
 * Writes a single routine into a backup file the app can share, and returns that file. The
 * published app called this a quick export.
 */
fun interface ExportRoutineUseCase {
    suspend fun exportRoutine(routineID: Long): BackupFile
}

/** What a backup file holds, read from its manifest without importing anything. */
fun interface ReadBackupContentsUseCase {
    suspend fun readBackupContents(location: BackupLocation): Set<BackupDataType>
}

/** Restores the selected data from a backup file, replacing whatever it collides with. */
fun interface ImportBackupUseCase {
    suspend fun importBackup(location: BackupLocation, types: Set<BackupDataType>)
}

/**
 * The location another app may read a backup file through. Files the app wrote to its own storage
 * are not reachable from outside until this has translated them.
 */
fun interface GetShareableLocationUseCase {
    suspend fun getShareableLocation(location: BackupLocation): BackupLocation
}

/** The name a directory should be shown under, or `null` if it can no longer be reached. */
fun interface GetDirectoryNameUseCase {
    suspend fun getDirectoryName(location: BackupLocation): String?
}

/** Remembers a directory the user picked, so later runs can still write to it. */
fun interface PersistDirectoryAccessUseCase {
    fun persistDirectoryAccess(location: BackupLocation)
}
