package com.patrykandpatryk.liftapp.core.search

import org.junit.Test
import kotlin.test.assertContentEquals

class SearchAlgorithmImplTest {

    private val entities = listOf("Apple", "Cherry", "Banana", "Pineapple")
    private val searchAlgorithm = SearchAlgorithmImpl()

    private fun search(query: String) = searchAlgorithm(
        query = query,
        entities = entities,
        selector = { entity -> entity },
    )

    @Test
    fun `Letter case is ignored`() {
        assertContentEquals(
            expected = listOf("Banana"),
            actual = search(query = "banana"),
        )
    }

    @Test
    fun `Typos are handled properly`() {
        assertContentEquals(
            expected = listOf("Pineapple"),
            actual = search(query = "Ppneapple"),
        )
    }

    @Test
    fun `Results are correctly ordered`() {
        assertContentEquals(
            expected = listOf("Apple", "Pineapple"),
            actual = search(query = "apple"),
        )
    }
}
