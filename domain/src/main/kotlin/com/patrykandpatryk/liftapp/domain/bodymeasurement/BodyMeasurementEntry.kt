package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.format.FormattedDate

class BodyMeasurementEntry(
    val id: Long,
    val value: BodyMeasurementValue,
    val formattedDate: FormattedDate,
)
