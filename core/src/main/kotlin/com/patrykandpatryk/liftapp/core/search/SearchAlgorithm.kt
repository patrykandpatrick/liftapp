package com.patrykandpatryk.liftapp.core.search

interface SearchAlgorithm {
    operator fun <T> invoke(
        query: String,
        items: List<T>,
        selector: (T) -> String,
    ): Pair<List<T>, List<IntRange>>

    data class SearchResult<T>(
        val item: T,
        val queryMatchPosition: IntRange,
        private val isQueryMatchFuzzy: Boolean,
        private val isQueryMatchPositionNatural: Boolean,
    ) : Comparable<SearchResult<*>> {
        override fun compareTo(other: SearchResult<*>) =
            compareValuesBy(
                this,
                other,
                { !it.isQueryMatchFuzzy },
                { it.isQueryMatchPositionNatural },
                { -it.queryMatchPosition.first },
            )
    }
}
