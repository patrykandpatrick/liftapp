package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import java.time.LocalDateTime

@Immutable
data class ScreenState(
    val bodyMeasurementID: Long,
    val name: String,
    val entries: List<Entry>,
    val modelProducer: CartesianChartModelProducer,
    val valueUnit: String,
) {

    @Immutable data class Entry(val id: Long, val value: String, val date: LocalDateTime)
}
