package com.patrykandpatryk.liftapp.core.mapper

import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyValues
import com.patrykandpatryk.liftapp.domain.extension.getOrPut
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.entryOf
import javax.inject.Inject

class BodyEntriesToChartEntriesMapper @Inject constructor() {

    operator fun invoke(input: List<BodyEntry>): List<List<ChartEntry>> =
        input.foldIndexed(ArrayList<ArrayList<ChartEntry>>()) { index, chartEntries, entry ->

            val reversedIndex = input.lastIndex - index

            when (val values = entry.values) {
                is BodyValues.Double -> {

                    chartEntries
                        .getOrPut(0) { ArrayList() }
                        .add(entryOf(reversedIndex, values.left))

                    chartEntries
                        .getOrPut(1) { ArrayList() }
                        .add(entryOf(reversedIndex, values.right))
                }
                is BodyValues.Single -> {

                    chartEntries
                        .getOrPut(0) { ArrayList() }
                        .add(entryOf(reversedIndex, values.value))
                }
            }

            chartEntries
        }
}
