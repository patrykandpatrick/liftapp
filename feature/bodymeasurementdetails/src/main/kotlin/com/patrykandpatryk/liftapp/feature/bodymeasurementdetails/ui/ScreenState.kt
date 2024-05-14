package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

sealed class ScreenState(
    open val bodyMeasurementID: Long,
    open val name: String,
    open val entries: List<Entry>,
    open val chartEntryModelProducer: ChartEntryModelProducer,
    open val newEntry: NewEntry?,
) {

    @Stable
    data class Loading(
        override val bodyMeasurementID: Long,
        override val chartEntryModelProducer: ChartEntryModelProducer,
    ) : ScreenState(
        bodyMeasurementID = bodyMeasurementID,
        name = "",
        entries = emptyList(),
        chartEntryModelProducer = chartEntryModelProducer,
        newEntry = null,
    )

    @Stable
    data class Populated(
        override val bodyMeasurementID: Long,
        override val name: String,
        override val entries: List<Entry>,
        override val chartEntryModelProducer: ChartEntryModelProducer,
        override val newEntry: NewEntry?,
    ) : ScreenState(
        bodyMeasurementID = bodyMeasurementID,
        name = "",
        entries = entries,
        chartEntryModelProducer = chartEntryModelProducer,
        newEntry = newEntry,
    )

    @Immutable
    data class Entry(
        val id: Long,
        val value: String,
        val date: String,
        val isExpanded: Boolean,
    )

    @Immutable
    data class NewEntry(
        val bodyMeasurementID: Long,
        val bodyMeasurementEntryID: Long? = null,
    )
}
