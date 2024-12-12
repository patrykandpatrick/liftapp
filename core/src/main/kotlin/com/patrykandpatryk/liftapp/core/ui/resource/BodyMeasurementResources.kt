package com.patrykandpatryk.liftapp.core.ui.resource

import androidx.annotation.DrawableRes
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType

val BodyMeasurementType.iconRes: Int
    @DrawableRes
    get() =
        when (this) {
            BodyMeasurementType.Weight -> R.drawable.ic_weightscale_outline
            BodyMeasurementType.Length -> R.drawable.ic_distance
            BodyMeasurementType.LengthTwoSides -> R.drawable.ic_distance
            BodyMeasurementType.Percentage -> R.drawable.ic_donut
        }
