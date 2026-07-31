package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.compose.ui.graphics.vector.ImageVector
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Percent
import com.patrykandpatrick.liftapp.ui.icons.Ruler
import com.patrykandpatrick.liftapp.ui.icons.Scale

val BodyMeasurementType.icon: ImageVector
    get() =
        when (this) {
            BodyMeasurementType.Weight -> LiftAppIcons.Scale
            BodyMeasurementType.Length -> LiftAppIcons.Ruler
            BodyMeasurementType.LengthTwoSides -> LiftAppIcons.Ruler
            BodyMeasurementType.Percentage -> LiftAppIcons.Percent
        }
