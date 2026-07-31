package com.patrykandpatrick.liftapp.feature.home

import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

/**
 * Emits the current local date and updates shortly after each minute boundary.
 *
 * Polling also handles wall-clock and time-zone changes without relying on an Android broadcast.
 */
fun currentDateFlow(): Flow<LocalDate> = flow {
    while (true) {
        emit(LocalDate.now())
        delay((60 - LocalTime.now().second).seconds)
    }
}
    .distinctUntilChanged()
