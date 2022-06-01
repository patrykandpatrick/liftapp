package pl.patrykgoworowski.mintlift.domain.mapper

interface Mapper<I, O> {

    fun map(input: I): O

    operator fun invoke(input: I): O = map(input)
}
