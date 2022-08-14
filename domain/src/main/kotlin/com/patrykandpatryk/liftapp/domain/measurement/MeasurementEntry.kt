package com.patrykandpatryk.liftapp.domain.measurement

import java.util.Calendar

class MeasurementEntry(
    val values: MeasurementValues,
    val type: MeasurementType,
    val timestamp: Calendar,
)
