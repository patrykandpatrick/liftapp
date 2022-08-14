package com.patrykandpatryk.liftapp.domain.body

import java.util.Calendar

class BodyEntry(
    val values: BodyValues,
    val type: BodyType,
    val timestamp: Calendar,
)
