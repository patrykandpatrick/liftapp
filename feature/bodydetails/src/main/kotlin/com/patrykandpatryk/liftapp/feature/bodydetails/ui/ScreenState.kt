package com.patrykandpatryk.liftapp.feature.bodydetails.ui

import androidx.compose.runtime.Stable
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer

sealed class ScreenState(
    open val bodyId: Long,
    open val name: String,
    open val entries: List<Entry>,
    open val chartEntryModelProducer: ChartEntryModelProducer,
) {

    @Stable
    data class Loading(
        override val bodyId: Long,
        override val chartEntryModelProducer: ChartEntryModelProducer,
    ) : ScreenState(
        bodyId = bodyId,
        name = "",
        entries = emptyList(),
        chartEntryModelProducer = chartEntryModelProducer,
    )

    @Stable
    data class Populated(
        override val bodyId: Long,
        override val name: String,
        override val entries: List<Entry>,
        override val chartEntryModelProducer: ChartEntryModelProducer,
    ) : ScreenState(
        bodyId = bodyId,
        name = "",
        entries = entries,
        chartEntryModelProducer = chartEntryModelProducer,
    )

    @Stable
    data class Entry(
        val id: Long,
        val value: String,
        val date: String,
        val isExpanded: Boolean,
    )
}
