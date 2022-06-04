package com.patrykandpatryk.liftapp.core.extension

import androidx.annotation.StringRes
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit

inline val DistanceUnit.stringResourceId: Int
    @StringRes
    get() = when (this) {
        DistanceUnit.Kilometers -> R.string.kilometer_unit
        DistanceUnit.Miles -> R.string.mile_unit
    }

inline val MassUnit.stringResourceId: Int
    @StringRes
    get() = when (this) {
        MassUnit.Kilograms -> R.string.kilogram_unit
        MassUnit.Pounds -> R.string.pound_unit
    }
