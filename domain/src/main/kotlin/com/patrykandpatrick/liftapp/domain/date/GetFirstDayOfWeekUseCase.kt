package com.patrykandpatrick.liftapp.domain.date

import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow

fun interface GetFirstDayOfWeekUseCase {
    fun getFirstDayOfWeek(): Flow<DayOfWeek>
}

operator fun GetFirstDayOfWeekUseCase.invoke(): Flow<DayOfWeek> = getFirstDayOfWeek()
