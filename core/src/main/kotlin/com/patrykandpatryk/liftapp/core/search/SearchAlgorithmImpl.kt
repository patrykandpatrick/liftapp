package com.patrykandpatryk.liftapp.core.search

import com.patrykandpatryk.liftapp.domain.extension.rangeOfLength
import javax.inject.Inject
import kotlin.math.min

class SearchAlgorithmImpl @Inject constructor() : SearchAlgorithm {
    override operator fun <T> invoke(
        query: String,
        items: List<T>,
        selector: (T) -> String,
    ): Pair<List<T>, List<IntRange>> {
        if (query.isEmpty()) return items to List(items.size) { IntRange.EMPTY }
        val matches = mutableListOf<T>()
        val queryMatchPositions = mutableListOf<IntRange>()
        items
            .mapNotNull { item ->
                val (queryMatchPosition, isQueryMatchFuzzy) =
                    item.getQueryMatchPosition(query, selector) ?: return@mapNotNull null
                SearchAlgorithm.SearchResult(
                    item,
                    queryMatchPosition,
                    isQueryMatchFuzzy,
                    item.isQueryMatchPositionNatural(queryMatchPosition.first, selector),
                )
            }
            .sortedDescending()
            .forEach { searchResult ->
                matches += searchResult.item
                queryMatchPositions += searchResult.queryMatchPosition
            }
        return matches to queryMatchPositions
    }

    private inline fun <T> T.getQueryMatchPosition(
        query: String,
        selector: (T) -> String,
    ): Pair<IntRange, Boolean>? {
        val candidate = selector(this)
        candidate.indexOf(string = query, ignoreCase = true).also {
            if (it != -1) return it.rangeOfLength(query.length) to false
        }
        return getFuzzyQueryMatchPosition(query, candidate)
    }

    private fun getFuzzyQueryMatchPosition(
        query: String,
        candidate: String,
    ): Pair<IntRange, Boolean>? {
        val maxMismatchCount = getMaxQueryMismatchCount(query.length)
        candidate
            .takeIf { query.length >= FUZZY_SEARCH_MIN_LENGTH }
            ?.indices
            ?.take((candidate.length - query.length + 1).coerceAtLeast(0))
            ?.forEach { potentialMatchPositionStart ->
                var mismatchCount = 0
                val substring =
                    candidate
                        .substring(potentialMatchPositionStart.rangeOfLength(query.length - 1))
                        .lowercase()
                substring.indices.forEach { charIndex ->
                    val mismatch = query[charIndex].lowercaseChar() != substring[charIndex]
                    val noSwap =
                        charIndex == 0 ||
                            query[charIndex].lowercaseChar() != substring[charIndex - 1] ||
                            query[charIndex - 1].lowercaseChar() != substring[charIndex]
                    if (mismatch && noSwap) mismatchCount++
                }
                if (mismatchCount <= maxMismatchCount) {
                    return potentialMatchPositionStart.rangeOfLength(
                        min(query.length, candidate.length)
                    ) to true
                }
            }
        return null
    }

    private inline fun <T> T.isQueryMatchPositionNatural(
        matchPositionStart: Int,
        selector: (T) -> String,
    ) =
        matchPositionStart == 0 ||
            selector(this)[matchPositionStart - 1] in beforeNaturalMatchPosition

    private fun getMaxQueryMismatchCount(queryLength: Int) =
        (queryLength - FUZZY_SEARCH_MIN_LENGTH) / MAX_MISMATCH_COUNT_STEP_LENGTH + 1

    private companion object {
        const val FUZZY_SEARCH_MIN_LENGTH = 3
        const val MAX_MISMATCH_COUNT_STEP_LENGTH = 4
        val beforeNaturalMatchPosition = listOf(' ', '-')
    }
}
