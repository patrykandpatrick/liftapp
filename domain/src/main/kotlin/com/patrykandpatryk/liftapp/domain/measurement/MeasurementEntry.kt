package com.patrykandpatryk.liftapp.domain.measurement

import java.util.Date

class MeasurementEntry(
    val values: MeasurementValues,
    val type: MeasurementType,
    val timestamp: Date,
)
