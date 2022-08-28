package com.patrykandpatryk.liftapp.domain.extension

import com.patrykmichalik.opto.domain.Preference
import kotlinx.coroutines.flow.first

suspend fun <C, S, K> Preference<C, S, K>.getFirst(): C = get().first()
