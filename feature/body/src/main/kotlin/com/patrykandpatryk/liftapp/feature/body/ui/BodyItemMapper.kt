package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.annotation.DrawableRes
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.body.BodyType
import com.patrykandpatryk.liftapp.domain.body.BodyWithLatestEntry
import javax.inject.Inject

class BodyItemMapper @Inject constructor() : Mapper<BodyWithLatestEntry, BodyItem> {

    override suspend fun map(input: BodyWithLatestEntry): BodyItem = BodyItem(
        id = input.id,
        iconRes = input.type.iconRes,
        title = input.name,
        latestRecord = input
            .latestEntry
            ?.let { entry ->
                BodyItem.LatestRecord(
                    formattedDate = entry.formattedDate,
                    value = entry.values.toString(), // FIXME
                )
            },
    )

    private val BodyType.iconRes: Int
        @DrawableRes get() = when (this) {
            BodyType.Weight -> R.drawable.ic_weightscale_outline
            BodyType.Length -> R.drawable.ic_distance
            BodyType.LengthTwoSides -> R.drawable.ic_distance
            BodyType.Percentage -> R.drawable.ic_donut
        }
}
