package com.patrykandpatryk.liftapp.core.logging

import com.patrykandpatryk.liftapp.domain.exception.DisplayableException
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import javax.inject.Inject

class DisplayableExceptionMapper @Inject constructor() : Mapper<DisplayableException, String> {

    override suspend fun map(input: DisplayableException): String = when (input) {
        is DisplayableException.Text -> input.message
    }
}
