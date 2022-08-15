package com.patrykandpatryk.liftapp.domain.mapper

interface Mapper<I, O> {

    suspend fun map(input: I): O

    suspend operator fun invoke(input: I): O = map(input)

    suspend operator fun invoke(input: Iterable<I>): List<O> = input.map { map(it) }
}
