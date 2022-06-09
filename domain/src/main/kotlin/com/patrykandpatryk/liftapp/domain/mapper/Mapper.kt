package com.patrykandpatryk.liftapp.domain.mapper

interface Mapper<I, O> {

    fun map(input: I): O

    operator fun invoke(input: I): O = map(input)

    operator fun invoke(input: Iterable<I>): List<O> = input.map(::map)
}
