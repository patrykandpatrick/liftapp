package com.patrykandpatryk.liftapp.domain.body

import com.patrykandpatryk.liftapp.domain.format.FormattedDate

class BodyEntry(
    val id: Long,
    val values: BodyValues,
    val formattedDate: FormattedDate,
)
