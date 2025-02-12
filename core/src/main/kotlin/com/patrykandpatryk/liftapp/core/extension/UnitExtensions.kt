package com.patrykandpatryk.liftapp.core.extension

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.extension.getTypeErrorMessage
import com.patrykandpatryk.liftapp.domain.unit.EnergyUnit
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit

inline val ValueUnit.stringResourceId: Int
    @StringRes
    get() =
        when (this) {
            LongDistanceUnit.Kilometer -> R.string.unit_kilometer
            LongDistanceUnit.Mile -> R.string.unit_mile
            MediumDistanceUnit.Meter -> R.string.unit_meter
            MediumDistanceUnit.Foot -> R.string.unit_foot
            ShortDistanceUnit.Centimeter -> R.string.unit_centimeter
            ShortDistanceUnit.Inch -> R.string.unit_inch
            MassUnit.Kilograms -> R.string.unit_kilogram
            MassUnit.Pounds -> R.string.unit_pound
            PercentageUnit -> R.string.unit_percentage
            EnergyUnit.KiloCalorie -> R.string.energy_unit_kcal
            else -> error(getTypeErrorMessage(unit = this))
        }

inline val HourFormat.stringResourceId: Int
    @StringRes
    get() =
        when (this) {
            HourFormat.Auto -> R.string.settings_hour_format_option_auto
            HourFormat.H12 -> R.string.settings_hour_format_option_12
            HourFormat.H24 -> R.string.settings_hour_format_option_24
        }

@Composable fun ValueUnit.prettyString(): String = stringResource(stringResourceId)
