package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.annotation.DrawableRes

data class BodyItem(
    val id: Long,
    @DrawableRes val iconRes: Int,
    val title: String,
    val latestRecord: LatestRecord?,
) {

    data class LatestRecord(
        val date: String,
        val value: String,
    )
}
