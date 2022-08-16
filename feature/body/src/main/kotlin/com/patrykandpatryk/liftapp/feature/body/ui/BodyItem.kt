package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.annotation.DrawableRes
import com.patrykandpatryk.liftapp.domain.format.FormattedDate

data class BodyItem(
    val id: Long,
    @DrawableRes val iconRes: Int,
    val title: String,
    val latestRecord: LatestRecord?,
) {

    data class LatestRecord(
        val formattedDate: FormattedDate,
        val value: String,
    )
}
