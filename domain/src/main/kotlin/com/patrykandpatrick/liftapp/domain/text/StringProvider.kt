package com.patrykandpatrick.liftapp.domain.text

import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.unit.ValueUnit

interface StringProvider {
    val name: String

    val list: String

    val dateFormatDay: String

    val dateFormatDayMonth: String

    val dateFormatWeekdayDayMonth: String

    val dateFormatDayMonthYear: String

    val dateWeekdayDayMonthYear: String

    val dateMonthYear: String

    val dateYear: String

    val errorMustBeHigherThanZero: String

    val hoursShort: String

    val minutesShort: String

    val secondsShort: String

    val hoursMedium: String

    val minutesMedium: String

    val secondsMedium: String

    /** The name a backup holding everything gets, without the date or the extension. */
    val backupNameFull: String

    /** The name a scheduled backup gets, without the date or the extension. */
    val backupNameAutomatic: String

    /** Trails the data types in the name of a backup holding only some of them. */
    val backupNameSuffix: String

    /** The date pattern that goes into a backup's file name. */
    val backupNameDateFormat: String

    val errorBackupFileUnsupported: String

    val errorBackupFileUnreadable: String

    val backupExportSucceeded: String

    val backupImportSucceeded: String

    val errorBackupExportFailed: String

    val errorBackupImportFailed: String

    val backupNotificationChannelName: String

    val backupFailedNotificationTitle: String

    val backupFailedNotificationBody: String

    fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean = true): String

    /**
     * A data type as it appears in a backup's file name, where it modifies "backup" and so takes an
     * attributive form: "Routine backup", not "Routines backup".
     *
     * [listContinuation] asks for the form that follows another entry in a list, which some
     * languages capitalize differently than the form that opens one.
     */
    fun getBackupNameDataType(type: BackupDataType, listContinuation: Boolean = false): String

    /** Joins [items] the way the current locale writes a list, serial comma and all. */
    fun formatList(items: List<String>): String

    fun getRepsString(reps: Int): String

    fun quoted(value: String): String

    fun getErrorNameTooLong(actual: Int, limit: Int = Constants.Input.NAME_MAX_CHARS): String

    fun getErrorCannotBeEmpty(name: String): String

    fun getMuscleName(muscle: Muscle): String

    fun getMuscleList(muscles: List<Muscle>): String

    fun getResolvedName(name: Name): String

    fun fieldTooShort(actual: Int, minLength: Int): String

    fun fieldTooLong(actual: Int, maxLength: Int): String

    fun valueTooSmall(minValue: String): String

    fun valueTooBig(maxValue: String): String

    fun valueNotValidNumber(): String

    fun fieldCannotBeEmpty(): String

    fun fieldMustBeHigherThanZero(): String

    fun fieldMustBeHigherOrEqualTo(value: String): String

    fun doesNotEqual(formula: String, actual: String): String
}

inline fun StringProvider.getErrorCannotBeEmpty(getName: StringProvider.() -> String): String =
    getErrorCannotBeEmpty(getName())
