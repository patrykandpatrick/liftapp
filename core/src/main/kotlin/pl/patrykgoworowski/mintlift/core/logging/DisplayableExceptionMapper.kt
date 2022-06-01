package pl.patrykgoworowski.mintlift.core.logging

import pl.patrykgoworowski.mintlift.domain.exception.DisplayableException
import pl.patrykgoworowski.mintlift.domain.mapper.Mapper
import javax.inject.Inject

class DisplayableExceptionMapper @Inject constructor() : Mapper<DisplayableException, String> {

    override fun map(input: DisplayableException): String = when (input) {
        is DisplayableException.Text -> input.message
    }
}
