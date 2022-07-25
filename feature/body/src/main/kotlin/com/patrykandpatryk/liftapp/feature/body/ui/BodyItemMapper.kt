package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.annotation.DrawableRes
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementType
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import javax.inject.Inject

class BodyItemMapper @Inject constructor() : Mapper<MeasurementWithLatestEntry, BodyItem> {

    override fun map(input: MeasurementWithLatestEntry): BodyItem = BodyItem(
        id = input.id,
        iconRes = input.type.iconRes,
        title = input.name,
        latestRecord = input
            .latestEntry
            ?.let { entry ->
                BodyItem.LatestRecord(
                    date = entry.timestamp.toString(), // FIXME
                    value = entry.values.toString(), // FIXME
                )
            },
    )

    private val MeasurementType.iconRes: Int
        @DrawableRes get() = when (this) {
            MeasurementType.Weight -> R.drawable.ic_weightscale
            MeasurementType.Length -> R.drawable.ic_distance
            MeasurementType.LengthTwoSides -> R.drawable.ic_distance
            MeasurementType.Percentage -> R.drawable.ic_donut
        }
}
