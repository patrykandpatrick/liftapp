package com.patrykandpatryk.liftapp.domain.date

import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetIs24HUseCase @Inject constructor(
    private val preferences: PreferenceRepository,
) {

    operator fun invoke(): Flow<Boolean> = preferences.is24H
}
