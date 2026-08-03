package com.patrykandpatrick.liftapp.feature.settings.model

import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import java.time.DayOfWeek

sealed interface Action {

    data class SetMassUnit(val massUnit: MassUnit) : Action

    data class SetDistanceUnit(val distanceUnit: LongDistanceUnit) : Action

    data class SetHourFormat(val hourFormat: HourFormat) : Action

    data class SetFirstDayOfWeek(val firstDayOfWeek: DayOfWeek) : Action

    data class SetTheme(val theme: Theme) : Action

    data object AutomaticBackup : Action

    data object OpenSourceLicenses : Action

    data object PopBackStack : Action
}
