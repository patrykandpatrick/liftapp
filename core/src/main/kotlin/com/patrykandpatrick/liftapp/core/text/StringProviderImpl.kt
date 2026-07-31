package com.patrykandpatrick.liftapp.core.text

import android.content.Context
import android.icu.text.ListFormatter
import androidx.annotation.StringRes
import com.patrykandpatrick.liftapp.core.R.plurals
import com.patrykandpatrick.liftapp.core.R.string
import com.patrykandpatrick.liftapp.core.extension.stringResourceId
import com.patrykandpatrick.liftapp.core.ui.resource.listContinuationStringRes
import com.patrykandpatrick.liftapp.core.ui.resource.nameListContinuationStringRes
import com.patrykandpatrick.liftapp.core.ui.resource.nameStringRes
import com.patrykandpatrick.liftapp.core.ui.resource.stringRes
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.domain.unit.ValueUnit
import javax.inject.Inject

class StringProviderImpl @Inject constructor(private val context: Context) : StringProvider {

    override val name: String
        get() = string(string.generic_name)

    override val list: String
        get() = string(string.generic_list)

    override val dateFormatDay: String
        get() = string(string.date_day)

    override val dateFormatDayMonth: String
        get() = string(string.date_day_month)

    override val dateFormatWeekdayDayMonth: String
        get() = string(string.date_weekday_day_month)

    override val errorMustBeHigherThanZero: String
        get() = string(string.error_must_be_higher_than_zero)

    override val dateFormatDayMonthYear: String
        get() = string(string.date_day_month_year)

    override val dateWeekdayDayMonthYear: String
        get() = string(string.date_weekday_day_month_year)

    override val dateMonthYear: String
        get() = string(string.date_month_year)

    override val dateYear: String
        get() = string(string.date_year)

    override val hoursShort: String
        get() = string(string.time_hours_short)

    override val minutesShort: String
        get() = string(string.time_minutes_short)

    override val secondsShort: String
        get() = string(string.time_seconds_short)

    override val hoursMedium: String
        get() = string(string.time_hours_medium)

    override val minutesMedium: String
        get() = string(string.time_minutes_medium)

    override val secondsMedium: String
        get() = string(string.time_seconds_medium)

    override val backupNameFull: String
        get() = string(string.backup_name_full)

    override val backupNameAutomatic: String
        get() = string(string.backup_name_automatic)

    override val backupNameSuffix: String
        get() = string(string.backup_name_suffix)

    override val backupNameDateFormat: String
        get() = string(string.backup_name_date_format)

    override val errorBackupFileUnsupported: String
        get() = string(string.backup_error_file_unsupported)

    override val errorBackupFileUnreadable: String
        get() = string(string.backup_error_file_unreadable)

    override val backupExportSucceeded: String
        get() = string(string.backup_export_success)

    override val backupImportSucceeded: String
        get() = string(string.backup_import_success)

    override val errorBackupExportFailed: String
        get() = string(string.backup_error_export_failed)

    override val errorBackupImportFailed: String
        get() = string(string.backup_error_import_failed)

    override val backupNotificationChannelName: String
        get() = string(string.backup_notification_channel)

    override val backupFailedNotificationTitle: String
        get() = string(string.backup_notification_failed_title)

    override val backupFailedNotificationBody: String
        get() = string(string.backup_notification_failed_body)

    override fun getBackupNameDataType(type: BackupDataType, listContinuation: Boolean): String =
        string(if (listContinuation) type.nameListContinuationStringRes else type.nameStringRes)

    override fun formatList(items: List<String>): String =
        ListFormatter.getInstance(context.resources.configuration.locales[0]).format(items)

    override fun getDisplayUnit(unit: ValueUnit, respectLeadingSpaceSetting: Boolean): String =
        string(unit.stringResourceId).let { displayUnit ->
            if (unit.hasLeadingSpace && respectLeadingSpaceSetting) " $displayUnit" else displayUnit
        }

    override fun getRepsString(reps: Int): String =
        context.resources.getQuantityString(plurals.rep_count, reps)

    override fun quoted(value: String): String = string(string.generic_quoted, value)

    override fun getErrorNameTooLong(actual: Int, limit: Int): String =
        string(string.error_name_too_long, actual, limit)

    override fun getErrorCannotBeEmpty(name: String): String = string(string.error_x_empty, name)

    override fun getMuscleName(muscle: Muscle): String = string(muscle.stringRes)

    override fun getMuscleList(muscles: List<Muscle>): String =
        formatList(
            muscles.mapIndexed { index, muscle ->
                string(if (index == 0) muscle.stringRes else muscle.listContinuationStringRes)
            }
        )

    override fun getResolvedName(name: Name): String =
        when (name) {
            is Name.Raw -> name.value
            is Name.Resource -> context.getString(name.resource.resourceId)
        }

    override fun fieldTooShort(actual: Int, minLength: Int): String =
        string(string.error_field_too_short, minLength, actual, minLength)

    override fun fieldTooLong(actual: Int, maxLength: Int): String =
        string(string.error_field_too_long, maxLength, actual, maxLength)

    override fun valueTooSmall(minValue: String): String =
        string(string.error_value_too_small, minValue)

    override fun valueTooBig(maxValue: String): String =
        string(string.error_value_too_big, maxValue)

    override fun valueNotValidNumber(): String = string(string.error_value_not_valid_number)

    override fun fieldCannotBeEmpty(): String = string(string.error_field_cannot_be_empty, name)

    override fun fieldMustBeHigherThanZero(): String = string(string.error_must_be_higher_than_zero)

    override fun fieldMustBeHigherOrEqualTo(value: String): String =
        string(string.error_must_be_higher_than_or_equal_to, value)

    override fun doesNotEqual(formula: String, actual: String): String =
        string(string.error_does_not_equal, formula, actual)

    private fun string(@StringRes id: Int, vararg formatArgs: Any): String =
        if (formatArgs.isNotEmpty()) context.getString(id, *formatArgs) else context.getString(id)
}
