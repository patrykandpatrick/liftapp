package com.patrykandpatryk.liftapp.core.ui.resource

import androidx.annotation.DrawableRes
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.body.BodyType

val BodyType.iconRes: Int
    @DrawableRes get() = when (this) {
        BodyType.Weight -> R.drawable.ic_weightscale_outline
        BodyType.Length -> R.drawable.ic_distance
        BodyType.LengthTwoSides -> R.drawable.ic_distance
        BodyType.Percentage -> R.drawable.ic_donut
    }
