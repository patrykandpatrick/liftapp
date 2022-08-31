package com.patrykandpatryk.liftapp.feature.bodydetails.ui

import androidx.compose.runtime.Stable
import com.patrykandpatryk.vico.core.entry.ChartEntry

sealed class ScreenState(
    open val bodyId: Long,
    open val name: String,
    open val entries: List<Entry>,
    open val chartEntries: List<List<ChartEntry>>,
) {

    @Stable
    data class Loading(
        override val bodyId: Long,
    ) : ScreenState(
        bodyId = bodyId,
        name = "",
        entries = emptyList(),
        chartEntries = emptyList(),
    )

    @Stable
    data class Populated(
        override val bodyId: Long,
        override val name: String,
        override val entries: List<Entry>,
        override val chartEntries: List<List<ChartEntry>>,
    ) : ScreenState(
        bodyId = bodyId,
        name = "",
        entries = entries,
        chartEntries = chartEntries,
    )

    @Stable
    data class Entry(
        val id: Long,
        val value: String,
        val date: String,
        val isExpanded: Boolean,
    )
}
