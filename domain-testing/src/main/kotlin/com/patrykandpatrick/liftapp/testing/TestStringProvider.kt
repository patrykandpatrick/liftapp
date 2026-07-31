package com.patrykandpatrick.liftapp.testing

import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.PercentageUnit
import com.patrykandpatrick.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.ValueUnit

object TestStringProvider : StringProvider {

    override val name: String = "Name"

    override val list: String = "List"

    override val dateFormatDay: String = "d"

    override val dateFormatDayMonth: String = "d MMMM"

    override val dateFormatWeekdayDayMonth: String = "EEEE, d MMMM"

    override val dateWeekdayDayMonthYear: String = "dd.MM.yyyy"

    override val dateFormatDayMonthYear: String = "d MMMM YYYY"

    override val dateMonthYear: String = "MMMM yyyy"

    override val dateYear: String = "yyyy"

    override val errorMustBeHigherThanZero: String = "The value must be higher than zero."

    override val hoursShort: String = "h"

    override val minutesShort: String = "m"

    override val secondsShort: String = "s"

    override val hoursMedium: String = "hr"

    override val minutesMedium: String = "min"

    override val secondsMedium: String = "sec"

    override val backupNameFull: String = "Full backup"

    override val backupNameAutomatic: String = "Auto backup"

    override val backupNameSuffix: String = "backup"

    override val backupNameDateFormat: String = " (d MMM yyyy)"

    override val errorBackupFileUnsupported: String = "This is not a LiftApp backup"

    override val errorBackupFileUnreadable: String = "The backup file could not be read"

    override val backupExportSucceeded: String = "Backup created"

    override val backupImportSucceeded: String = "Data restored"

    override val errorBackupExportFailed: String = "The backup could not be created"

    override val errorBackupImportFailed: String = "The backup could not be restored"

    override val backupNotificationChannelName: String = "Auto backup"

    override val backupFailedNotificationTitle: String = "Auto backup failed"

    override val backupFailedNotificationBody: String =
        "LiftApp no longer has access to the folder you chose"

    override fun getBackupNameDataType(type: BackupDataType, listContinuation: Boolean): String =
        when (type) {
            BackupDataType.Routines -> "Routine"
            BackupDataType.Workouts -> "Workout"
            BackupDataType.TrainingPlans -> "Training-plan"
            BackupDataType.BodyMeasurements -> "Body-measurement"
            BackupDataType.Settings -> "Settings"
        }.let { if (listContinuation) it.replaceFirstChar(Char::lowercaseChar) else it }

    /**
     * Approximates what `ListFormatter` does for English, serial comma included, so tests read the
     * way the app does. Joining on ", " instead would make every expectation a string no user ever
     * sees.
     */
    override fun formatList(items: List<String>): String =
        when (items.size) {
            0,
            1 -> items.joinToString(separator = "")
            2 -> "${items[0]} and ${items[1]}"
            else -> "${items.dropLast(1).joinToString(separator = ", ")}, and ${items.last()}"
        }

    override fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean): String =
        when (unit) {
            MassUnit.Kilograms -> "kg"
            MassUnit.Pounds -> "lb"
            LongDistanceUnit.Kilometer -> "km"
            LongDistanceUnit.Mile -> "mi"
            MediumDistanceUnit.Meter -> "m"
            MediumDistanceUnit.Foot -> "ft"
            ShortDistanceUnit.Centimeter -> "cm"
            ShortDistanceUnit.Inch -> "in"
            PercentageUnit -> "%"
            else -> getTypeErrorMessage(unit = unit)
        }.let { displayUnit -> if (unit.hasLeadingSpace) " $displayUnit" else displayUnit }

    override fun getRepsString(reps: Int): String =
        when (reps) {
            1 -> "rep"
            else -> "reps"
        }

    override fun quoted(value: String): String = "”%s“".format(value)

    override fun getErrorCannotBeEmpty(name: String): String = "%s cannot be empty.".format(name)

    override fun getMuscleName(muscle: Muscle): String = muscle.name

    override fun getMuscleList(muscles: List<Muscle>): String = formatList(muscles.map { it.name })

    override fun getErrorNameTooLong(actual: Int, limit: Int): String =
        "The name is too long (%1\$d/%2\$d).".format(actual, limit)

    override fun getResolvedName(name: Name): String =
        when (name) {
            is Name.Raw -> name.value
            is Name.Resource -> requireNotNull(name.resource.resourceId::class.simpleName)
        }

    override fun fieldCannotBeEmpty(): String = "This field cannot be empty."

    override fun fieldMustBeHigherThanZero(): String = "This field must be higher than zero."

    override fun fieldMustBeHigherOrEqualTo(value: String): String =
        "The value must be higher, or equal to %s.".format(value)

    override fun fieldTooShort(actual: Int, minLength: Int): String =
        "This field must be at least %d characters long.".format(minLength)

    override fun fieldTooLong(actual: Int, maxLength: Int): String =
        "This field must be at most %d characters long.".format(maxLength)

    override fun valueTooSmall(minValue: String): String =
        "This field must be at least %s.".format(minValue)

    override fun valueTooBig(maxValue: String): String =
        "This field must be at most %s.".format(maxValue)

    override fun valueNotValidNumber(): String = "This field must be a valid number."

    override fun doesNotEqual(formula: String, actual: String): String =
        "$formula does not equal $actual"
}
