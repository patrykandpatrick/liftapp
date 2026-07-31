package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType

data class BodyMeasurementListItem(
    val headline: String,
    val supportingText: String?,
    val bodyMeasurementID: Long,
    val bodyMeasurementType: BodyMeasurementType,
)
