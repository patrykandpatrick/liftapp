package com.patrykandpatryk.liftapp.core.search

interface SearchAlgorithm {

    operator fun <T> invoke(
        query: String,
        entities: List<T>,
        selector: (T) -> String,
    ): List<T>
}
