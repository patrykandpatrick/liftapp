package com.patrykandpatryk.liftapp.domain.format

import java.time.LocalDateTime
import javax.inject.Inject

class GetFormattedDateUseCase @Inject constructor(private val formatter: Formatter) {
    operator fun invoke(date: LocalDateTime): FormattedDate = formatter.getFormattedDate(date)
}
