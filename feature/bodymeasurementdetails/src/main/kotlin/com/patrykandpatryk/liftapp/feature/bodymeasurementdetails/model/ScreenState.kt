package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

@Immutable
data class ScreenState(
    val bodyMeasurementID: Long,
    val name: String,
    val entries: List<Entry>,
    val chartEntryModelProducer: ChartEntryModelProducer,
) {

    @Immutable
    data class Entry(val id: Long, val value: String, val date: String, val isExpanded: Boolean)
}
