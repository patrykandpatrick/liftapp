package com.patrykandpatrick.liftapp.functionality.backup.file

import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Names a backup after what is in it, the way the published app did: "Full backup (30 Jul
 * 2026).lfa", "Routine and workout backup (30 Jul 2026).lfa", "Auto backup (30 Jul 2026).lfa".
 *
 * The name is the only thing distinguishing a scheduled backup from a manual one, which is what
 * lets the retention sweep delete the app's own files and leave the user's alone.
 */
class BackupFileNamer @Inject constructor(private val stringProvider: StringProvider) {

    fun name(types: Set<BackupDataType>, automatic: Boolean, date: LocalDate): String {
        val stem =
            when {
                automatic -> stringProvider.backupNameAutomatic
                types.containsAll(BackupDataType.entries) -> stringProvider.backupNameFull
                else -> {
                    val names =
                        BackupDataType.entries
                            .filter { it in types }
                            .mapIndexed { index, type ->
                                stringProvider.getBackupNameDataType(
                                    type,
                                    listContinuation = index > 0,
                                )
                            }
                    "${stringProvider.formatList(names)} ${stringProvider.backupNameSuffix}"
                }
            }
        val formatter = DateTimeFormatter.ofPattern(stringProvider.backupNameDateFormat)
        val extension = if (automatic) AUTOMATIC_EXTENSION else BackupFormat.EXTENSION
        return "$stem${formatter.format(date)}$extension"
    }

    /** Whether [name] belongs to a backup this app scheduled rather than one the user asked for. */
    fun isAutomatic(name: String): Boolean = AUTOMATIC_NAME.matches(name)

    private companion object {
        const val AUTOMATIC_EXTENSION = ".auto${BackupFormat.EXTENSION}"
        val AUTOMATIC_NAME = Regex(".*\\.auto(?: \\d+)?\\.lfa", RegexOption.IGNORE_CASE)
    }
}
