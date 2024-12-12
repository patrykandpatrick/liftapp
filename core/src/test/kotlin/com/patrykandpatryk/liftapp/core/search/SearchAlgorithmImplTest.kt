package com.patrykandpatryk.liftapp.core.search

import kotlin.test.assertContentEquals
import org.junit.Test

class SearchAlgorithmImplTest {
    private val items = listOf("Apple", "Cherry", "Banana", "Pineapple")
    private val searchAlgorithm = SearchAlgorithmImpl()

    private fun search(query: String) = searchAlgorithm(query, items) { it }

    @Test
    fun `Letter case is ignored`() {
        assertContentEquals(listOf("Banana"), search("banana").first)
    }

    @Test
    fun `Mistyped letters are handled properly`() {
        assertContentEquals(listOf("Pineapple"), search("Ppneapple").first)
    }

    @Test
    fun `Swapped letters are handled properly`() {
        assertContentEquals(listOf("Cherry"), search("Cheryr").first)
    }

    @Test
    fun `Results are correctly ordered`() {
        assertContentEquals(listOf("Apple", "Pineapple"), search("apple").first)
    }
}
