package com.patrykandpatrick.liftapp.domain.backup

import java.time.LocalDateTime

/** A backup file as it appears on storage, before anything inside it has been read. */
data class BackupFile(
    val location: BackupLocation,
    val name: String,
    val sizeBytes: Long,
    val lastModified: LocalDateTime,
)
