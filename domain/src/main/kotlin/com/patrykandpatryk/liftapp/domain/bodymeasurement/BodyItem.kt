package com.patrykandpatryk.liftapp.domain.body

import com.patrykandpatryk.liftapp.domain.format.FormattedDate

data class BodyItem(
    val id: Long,
    val type: BodyType,
    val title: String,
    val latestRecord: LatestRecord?,
) {

    data class LatestRecord(
        val formattedDate: FormattedDate,
        val value: String,
    )
}
