package com.patrykandpatryk.liftapp.domain.android

import kotlinx.coroutines.flow.StateFlow

interface IsDarkModeReceiver {

    operator fun invoke(): StateFlow<Boolean>
}
